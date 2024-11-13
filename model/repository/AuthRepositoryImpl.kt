package com.dacslab.android.sleeping.model.repository

import android.util.Log
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.SessionExpiredException
import com.dacslab.android.sleeping.model.source.local.AuthLocalDataSource
import com.dacslab.android.sleeping.model.source.local.Token
import com.dacslab.android.sleeping.model.source.remote.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository() {

    override suspend fun isUserAuthenticated(): Boolean {
        return authLocalDataSource.isUserAuthenticated()
    }

    override suspend fun login(
        userId: String, userPw: String
    ): Pair<Boolean, String> {
        val result = authRemoteDataSource.login(userId, userPw)

        return if (result.isSuccess) {
            val token = result.successData // successData는 Token? 타입임

            if (token != null) {
                // Token 객체를 저장
                authLocalDataSource.saveUserToken(token)
                val successMessage = result.message ?: "로그인 성공"
                Log.d("AuthRepositoryImpl", successMessage)
                Pair(true, successMessage)
            } else {
                // 성공 응답인데 Token이 없는 경우 예외 처리
                val errorMessage = "로그인 성공했으나 Token 정보가 없습니다."
                Log.d("AuthRepositoryImpl", errorMessage)
                Pair(false, errorMessage)
            }
        } else {
            val errorMessage = result.message ?: "로그인 실패"
            Log.d("AuthRepositoryImpl", "로그인 실패: $errorMessage")
            Pair(false, "로그인 실패: $errorMessage")
        }
    }


    override suspend fun register(user: User): Pair<Boolean, String> {
        val result = authRemoteDataSource.register(user)

        return if (result.isSuccess) {
            val successMessage = result.message ?: "회원가입 성공"
            Log.d("AuthRepositoryImpl", successMessage)
            Pair(true, successMessage)
        } else {
            val errorMessage = result.message ?: "회원가입 실패"
            Log.d("AuthRepositoryImpl", "회원가입 실패: $errorMessage")
            Pair(false, "회원가입 실패: $errorMessage")
        }
    }

    override suspend fun getUserToken(): Token? {
        return if (authLocalDataSource.isUserAuthenticated()) {
            authLocalDataSource.getUserToken()
        } else {
            null
        }
    }


    override suspend fun checkToken() {
        try {
            val result = authRemoteDataSource.check()
            if (result.isSuccess) {
                Log.d("AuthRepositoryImpl", "토큰이 유효합니다.")
            } else {
                val errorMessage = result.message ?: "토큰 유효성 검사 실패"
                Log.d("AuthRepositoryImpl", errorMessage)
                // 토큰이 유효하지 않은 경우 로그아웃 처리
                logout()
                throw SessionExpiredException(errorMessage)
            }
        } catch (e: SessionExpiredException) {
            Log.d("AuthRepositoryImpl", "세션이 만료되었습니다.")
            // 세션 만료 예외 처리: 필요에 따라 상위 레벨로 예외 전달
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMessage = "네트워크 오류로 인해 토큰 유효성 검사를 수행할 수 없습니다."
            Log.d("AuthRepositoryImpl", errorMessage)
            throw Exception(errorMessage)
        }
    }




    override suspend fun logout() {
        authLocalDataSource.clearUserToken()
    }
}
