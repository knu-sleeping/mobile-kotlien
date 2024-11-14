package com.dacslab.android.sleeping.model.source.remote

import android.util.Log
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.BaseResponseImpl
import com.dacslab.android.sleeping.model.network.PasswordChangeRequest
import com.dacslab.android.sleeping.model.network.PasswordVerifyRequest
import com.dacslab.android.sleeping.model.network.SessionExpiredException
import com.dacslab.android.sleeping.model.network.UserApiService
import com.dacslab.android.sleeping.model.network.UserInfoResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.HttpException
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApiService: UserApiService
) {

    suspend fun getUserInfo(): UserInfoResponse {
        return try {
            val response = userApiService.userInfoAPI()

            if (response.isSuccessful) {
                // 서버에서 User 객체를 성공적으로 받아온 경우
                val user = response.body()  // User 타입으로 매핑됨

                // User 객체를 UserInfoResponse로 감싸 반환
                UserInfoResponse(
                    isSuccess = true,
                    message = "User info retrieved successfully",
                    userInfo = user
                )
            } else {
                // 실패한 경우, 에러 메시지 추출
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error : not 200"
                UserInfoResponse(
                    isSuccess = false,
                    message = errorMessage,
                    failData = errorBody
                )
            }
        } catch (e: HttpException) {
            // 예외 발생 시 에러 메시지 처리
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "HttpException error"
            UserInfoResponse(
                isSuccess = false,
                message = errorMessage,
                failData = errorBody
            )
        } catch (e: SessionExpiredException) {
            // 세션 만료 예외는 그대로 상위로 던짐
            throw e
        } catch (e: Exception) {
            // 네트워크 오류 등 일반적인 예외 처리
            e.printStackTrace()
            UserInfoResponse(
                isSuccess = false,
                message = "Network Error",
                failData = e.message
            )
        }
    }

    suspend fun passwordChange(
        currentPassword: String, newPassword: String
    ): BaseResponseImpl {
        return try {
            // 요청 객체 생성
            val request = PasswordChangeRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            // API 호출
            val response = userApiService.passwordChangeAPI(request)

            if (response.isSuccessful) {
                // 성공적인 응답을 받은 경우
                val responseBody = response.body()
                BaseResponseImpl(
                    isSuccess = true,
                    message = responseBody?.message,
                )
            } else {
                // 실패 응답 처리
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error: not 200"
                BaseResponseImpl(
                    isSuccess = false,
                    message = errorMessage,
                    failData = errorBody
                )
            }
        } catch (e: HttpException) {
            // HttpException 예외 처리
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "HttpException error"
            BaseResponseImpl(
                isSuccess = false,
                message = errorMessage,
                failData = errorBody
            )
        } catch (e: SessionExpiredException) {
            // 세션 만료 예외는 그대로 상위로 던짐
            throw e
        } catch (e: Exception) {
            // 네트워크 오류 등 일반적인 예외 처리
            e.printStackTrace()
            BaseResponseImpl(
                isSuccess = false,
                message = "Network Error",
                failData = e.message
            )
        }
    }

    suspend fun updateUserInfo(user: User): BaseResponseImpl {
        return try {
            val response = userApiService.updateUserInfoAPI(user)
            if (response.isSuccessful) {
                val responseBody = response.body()
                BaseResponseImpl(
                    isSuccess = true,
                    message = responseBody?.message
                )
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error: not 200"
                BaseResponseImpl(
                    isSuccess = false,
                    message = errorMessage,
                    failData = errorBody
                )
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "HttpException error"
            BaseResponseImpl(
                isSuccess = false,
                message = errorMessage,
                failData = errorBody
            )
        } catch (e: SessionExpiredException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            BaseResponseImpl(
                isSuccess = false,
                message = "Network Error",
                failData = e.message
            )
        }
    }

    suspend fun deleteAccount(): BaseResponseImpl {
        return try {
            val response = userApiService.deleteAccountAPI()  // 회원탈퇴 API 호출
            if (response.isSuccessful) {
                val responseBody = response.body()
                BaseResponseImpl(
                    isSuccess = true,
                    message = responseBody?.message
                )
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error: not 200"
                BaseResponseImpl(
                    isSuccess = false,
                    message = errorMessage,
                    failData = errorBody
                )
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "HttpException error"
            BaseResponseImpl(
                isSuccess = false,
                message = errorMessage,
                failData = errorBody
            )
        } catch (e: SessionExpiredException) {
            throw e  // 세션 만료 예외는 상위로 전달
        } catch (e: Exception) {
            e.printStackTrace()
            BaseResponseImpl(
                isSuccess = false,
                message = "Network Error",
                failData = e.message
            )
        }
    }

    suspend fun verifyPassword(password: String): BaseResponseImpl {
        return try {
            // API 호출
            val response = userApiService.verifyPasswordAPI(
                PasswordVerifyRequest(password)
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                BaseResponseImpl(
                    isSuccess = true,
                    message = responseBody?.message ?: "비밀번호 검증 성공"
                )
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, JsonObject::class.java).get("message").asString
                } ?: "Unknown error: not 200"
                BaseResponseImpl(
                    isSuccess = false,
                    message = errorMessage,
                    failData = errorBody
                )
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                Gson().fromJson(it, JsonObject::class.java).get("message").asString
            } ?: "HttpException error"
            BaseResponseImpl(
                isSuccess = false,
                message = errorMessage,
                failData = errorBody
            )
        } catch (e: SessionExpiredException) {
            throw e // 세션 만료 예외는 상위로 전달
        } catch (e: Exception) {
            e.printStackTrace()
            BaseResponseImpl(
                isSuccess = false,
                message = "Network Error",
                failData = e.message
            )
        }
    }


}