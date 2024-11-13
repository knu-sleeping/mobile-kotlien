package com.dacslab.android.sleeping.model.source.local

import android.content.Context
import android.content.SharedPreferences

// Token 데이터 클래스 정의
data class Token(
    val accessToken: String,
    val refreshToken: String,
    val userName: String?
)

class AuthLocalDataSource(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // 인증 상태 확인
    fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getBoolean("is_authenticated", false)
    }

    // 토큰 정보 저장
    fun saveUserToken(token: Token) {
        sharedPreferences.edit().apply {
            putBoolean("is_authenticated", true)
            putString("access_token", token.accessToken)
            putString("refresh_token", token.refreshToken)
            token.userName?.let {
                putString("user_name", it)
            }
            apply()
        }
    }

    // 토큰 정보 삭제
    fun clearUserToken() {
        sharedPreferences.edit().apply {
            remove("is_authenticated")
            remove("access_token")
            remove("refresh_token")
            remove("user_name")
            apply()
        }
    }

    // 저장된 토큰 정보 가져오기 (자동 로그인 등에 활용 가능)
    fun getUserToken(): Token? {
        if (!isUserAuthenticated()) return null

        val accessToken = sharedPreferences.getString("access_token", null) ?: return null
        val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null
        val userName = sharedPreferences.getString("user_name", null)

        return Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userName = userName
        )
    }
}
