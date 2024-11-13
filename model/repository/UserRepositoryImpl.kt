package com.dacslab.android.sleeping.model.repository

import android.util.Log
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.source.remote.UserRemoteDataSource;

import javax.inject.Inject;

class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource:UserRemoteDataSource
) : UserRepository() {

    override suspend fun getUserInfo(): Triple<Boolean, User?, String?> {
        val response = userRemoteDataSource.getUserInfo()

        return Triple(
            response.isSuccess,     // 성공 여부
            response.userInfo,      // User 객체 (성공 시 포함)
            response.message        // 메시지 (성공 또는 실패 메시지)
        )
    }

    override suspend fun passwordChange(
        currentPassword: String,
        newPassword: String
    ): Pair<Boolean, String> {
        val result = userRemoteDataSource.passwordChange(currentPassword, newPassword)

        return if (result.isSuccess) {
            val successMessage = result.message ?: "비밀번호 변경 성공"
            Log.d("UserRepositoryImpl", successMessage)
            Pair(true, successMessage)
        } else {
            val errorMessage = result.message ?: "비밀번호 변경 실패"
            Log.d("UserRepositoryImpl", "비밀번호 변경 실패: $errorMessage")
            Pair(false, errorMessage)
        }
    }
}