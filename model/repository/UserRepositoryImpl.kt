package com.dacslab.android.sleeping.model.repository

import android.util.Log
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.PasswordVerifyRequest
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

    override suspend fun updateUserInfo(user: User): Pair<Boolean, String?> {
        val result = userRemoteDataSource.updateUserInfo(user)

        return if (result.isSuccess) {
            val successMessage = result.message ?: "유저 정보 수정 성공"
            Log.d("UserRepositoryImpl", successMessage)
            Pair(true, successMessage)
        } else {
            val errorMessage = result.message ?: "유저 정보 수정 실패"
            Log.d("UserRepositoryImpl", "유저 정보 수정 실패: $errorMessage")
            Pair(false, errorMessage)
        }
    }

    override suspend fun deleteAccount(): Pair<Boolean, String> {
        val result = userRemoteDataSource.deleteAccount()
        return if (result.isSuccess) {
            val successMessage = result.message ?: "회원탈퇴 성공"
            Log.d("UserRepositoryImpl", successMessage)
            Pair(true, successMessage)
        } else {
            val errorMessage = result.message ?: "회원탈퇴 실패"
            Log.d("UserRepositoryImpl", "회원탈퇴 실패: $errorMessage")
            Pair(false, errorMessage)
        }
    }

    override suspend fun verifyPassword(password: String): Pair<Boolean, String> {
        val result = userRemoteDataSource.verifyPassword(password)

        return if (result.isSuccess) {
            val successMessage = result.message ?: "비밀번호 검증 성공"
            Log.d("UserRepositoryImpl", successMessage)
            Pair(true, successMessage)
        } else {
            val errorMessage = result.message ?: "비밀번호 검증 실패"
            Log.d("UserRepositoryImpl", "비밀번호 검증 실패: $errorMessage")
            Pair(false, errorMessage)
        }
    }
}