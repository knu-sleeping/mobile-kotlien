package com.dacslab.android.sleeping.model.network
import com.google.gson.annotations.SerializedName

// 로그인 요청에 사용할 데이터 클래스 정의
data class LoginRequest(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("user_pw")
    val userPw: String
)



