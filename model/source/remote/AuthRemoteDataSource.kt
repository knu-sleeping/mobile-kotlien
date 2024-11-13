package com.dacslab.android.sleeping.model.source.remote

import android.util.Log
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.AuthApiService
import com.dacslab.android.sleeping.model.network.BaseResponseImpl
import com.dacslab.android.sleeping.model.network.LoginRequest
import com.dacslab.android.sleeping.model.network.LoginResponse
import com.dacslab.android.sleeping.model.network.RegisterResponse
import com.dacslab.android.sleeping.model.network.SessionExpiredException
import com.dacslab.android.sleeping.model.source.local.Token
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import javax.inject.Inject


class AuthRemoteDataSource @Inject constructor(
    private val authApiService: AuthApiService
) {

    suspend fun login(userId: String, userPw: String): LoginResponse {
        return try {
            // 로그인 요청 전송 및 응답 처리
            val response = authApiService.loginAPI(LoginRequest(userId, userPw))

            if (response.isSuccessful) {
                val setCookieHeaders = response.headers().values("Set-Cookie")

                // 쿠키에서 토큰과 사용자 이름을 추출하여 Token 객체 생성
                var accessToken: String? = null
                var refreshToken: String? = null
                var userName: String? = null

                setCookieHeaders.forEach { cookie ->
                    when {
                        cookie.contains("accessToken") -> {
                            accessToken = cookie.split("=")[1].split(";")[0]
                        }

                        cookie.contains("refreshToken") -> {
                            refreshToken = cookie.split("=")[1].split(";")[0]
                        }

                        cookie.contains("user_name") -> {
                            userName = cookie.split("=")[1].split(";")[0]
                        }
                    }
                }

                // Token 객체 생성
                val token = if (accessToken != null && refreshToken != null) {
                    Token(
                        accessToken = accessToken!!,
                        refreshToken = refreshToken!!,
                        userName = userName
                    )
                } else {
                    null
                }

                // 성공 시 LoginResponse를 반환, successData에 Token 저장
                LoginResponse(
                    message = response.body()?.message,
                    cookies = token,
                    isSuccess = true
                )
            } else {
                // 에러 응답 본문에서 메시지 추출
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error"
                // 실패 시 LoginResponse를 반환, failData에 에러 메시지 저장
                LoginResponse(message = errorMessage, isSuccess = false)
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "Unknown error"
            // HttpException 발생 시 실패 응답
            LoginResponse(message = errorMessage, isSuccess = false)
        } catch (e: Exception) {
            e.printStackTrace()
            // 네트워크 오류 등 기타 예외 발생 시 실패 응답
            LoginResponse(message = "Network error", isSuccess = false)
        }
    }

    suspend fun register(user: User): RegisterResponse {
        return try {
            val response = authApiService.registerAPI(user)

            if (response.isSuccessful) {
                val successMessage = response.body()?.message ?: "User registered successfully"
                RegisterResponse(message = successMessage, isSuccess = true)
            } else {
                // 에러 응답 본문에서 메시지 추출
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error"
                RegisterResponse(message = errorMessage, isSuccess = false)
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "Unknown error"
            RegisterResponse(message = errorMessage, isSuccess = false)
        } catch (e: Exception) {
            e.printStackTrace()
            RegisterResponse(message = "Network Error", isSuccess = false)
        }
    }

    suspend fun check(): BaseResponseImpl {
        return try {
            Log.d("AuthRepository", "Sending token validity check request")

            // 토큰 유효성 검사 요청 전송
            val response = authApiService.tokenCheckAPI()

            Log.d("AuthRepository", "Response received. isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                Log.d("AuthRepository", "Token is valid")

                // 토큰이 유효할 경우 성공 메시지 반환
                BaseResponseImpl(
                    isSuccess = true,
                    message = response.body()?.message ?: "Token is valid",
                    successData = null,
                    failData = null
                )
            } else {
                Log.d("AuthRepository", "Token is invalid or other error occurred")

                // 토큰이 유효하지 않거나 기타 에러가 발생한 경우 실패 메시지 반환
                val errorBody = response.errorBody()?.string()
                Log.d("AuthRepository", "Error body: $errorBody")

                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error"

                Log.d("AuthRepository", "Parsed error message: $errorMessage")

                BaseResponseImpl(
                    isSuccess = false,
                    message = errorMessage,
                    successData = null,
                    failData = errorMessage
                )
            }
        } catch (e: SessionExpiredException) {
            // 세션 만료 예외는 그대로 상위로 던짐
            throw e
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "Unknown error"
            BaseResponseImpl(
                isSuccess = false,
                message = errorMessage,
                successData = null,
                failData = errorMessage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BaseResponseImpl(
                isSuccess = false,
                message = "Network error",
                successData = null,
                failData = "Network error"
            )
        }
    }

}

