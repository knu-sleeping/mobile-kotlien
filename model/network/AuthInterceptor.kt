package com.dacslab.android.sleeping.model.network

import android.util.Log
import com.dacslab.android.sleeping.BuildConfig
import com.dacslab.android.sleeping.model.source.local.AuthLocalDataSource
import com.dacslab.android.sleeping.model.source.local.Token
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
) : Interceptor {

    private val refreshApiService: RefreshApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApiService::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = authLocalDataSource.getUserToken()?.accessToken
        val isNoAuthRequired = isNoAuthRequired(request.url().encodedPath())

        if (!isNoAuthRequired) {
            request = addAuthorizationHeader(request, accessToken)
        }

        val response = chain.proceed(request)

        // 401 Unauthorized 응답 처리
        if (response.code() == 401) {
            response.close()
            return handleUnauthorized(chain, request, accessToken)
        }

        return response
    }

    private fun isNoAuthRequired(requestPath: String): Boolean {
        val noAuthRequiredEndpoints = listOf("/auth/login", "/auth/register")
        val requiresNoAuth = noAuthRequiredEndpoints.any { requestPath.contains(it) }

        // 디버깅용 로그 출력
        Log.d("AuthInterceptor", ">> Checking if no auth is required for path: $requestPath, requiresNoAuth?: $requiresNoAuth")
        return requiresNoAuth
    }

    private fun addAuthorizationHeader(request: okhttp3.Request, accessToken: String?): okhttp3.Request {
        return if (accessToken.isNullOrBlank()) {
            throw SessionExpiredException("로그인 필요")
        } else {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
    }

    private fun handleUnauthorized(chain: Interceptor.Chain, request: okhttp3.Request, accessToken: String?): Response {
        synchronized(this) {
            val currentAccessToken = authLocalDataSource.getUserToken()?.accessToken
            if (currentAccessToken == accessToken) {
                val newToken = runBlocking { getUpdateToken() }
                return if (newToken != null) {
                    retryRequestWithNewToken(chain, request, newToken.accessToken)
                } else {
                    throw SessionExpiredException("재로그인 필요")
                }
            }
            throw SessionExpiredException("재로그인 필요")
        }
    }

    private fun retryRequestWithNewToken(chain: Interceptor.Chain, request: okhttp3.Request, newAccessToken: String): Response {
        val newRequest = request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newAccessToken")
            .build()
        return chain.proceed(newRequest)
    }

    private suspend fun getUpdateToken(): Token? {
        val refreshToken = authLocalDataSource.getUserToken()?.refreshToken ?: return null
        Log.d("AuthInterceptor", "Attempting to refresh token with refreshToken")

        return try {
            val response = refreshApiService.refreshAPI("Bearer $refreshToken")
            if (response.isSuccessful) {
                parseTokenFromResponse(response.headers().values("Set-Cookie"))
            } else {
                handleRefreshFailure(response.code())
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Exception during token refresh: ${e.message}", e)
            null
        }
    }

    private fun parseTokenFromResponse(setCookieHeaders: List<String>): Token? {
        val cookies = mutableMapOf<String, String>()
        setCookieHeaders.forEach { cookie ->
            when {
                cookie.contains("access_token") -> cookies["accessToken"] = cookie.split("=")[1].split(";")[0]
                cookie.contains("refresh_token") -> cookies["refreshToken"] = cookie.split("=")[1].split(";")[0]
            }
        }

        return if (cookies["accessToken"] != null && cookies["refreshToken"] != null) {
            authLocalDataSource.saveUserToken(
                Token(
                    accessToken = cookies["accessToken"]!!,
                    refreshToken = cookies["refreshToken"]!!,
                    userName = authLocalDataSource.getUserToken()?.userName ?: ""
                )
            )
            Log.d("AuthInterceptor", "New token saved successfully.")
            authLocalDataSource.getUserToken()
        } else {
            Log.e("AuthInterceptor", "Token refresh failed: Token missing in response cookies.")
            null
        }
    }

    private fun handleRefreshFailure(responseCode: Int): Token? {
        return if (responseCode == 401) {
            Log.e("AuthInterceptor", "Refresh token expired. Login required.")
            throw SessionExpiredException("Session expired, 재로그인 필요")
        } else {
            Log.e("AuthInterceptor", "Unexpected error during token refresh. Response code: $responseCode")
            null
        }
    }
}
