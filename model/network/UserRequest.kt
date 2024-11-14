package com.dacslab.android.sleeping.model.network

import com.google.gson.annotations.SerializedName

data class PasswordChangeRequest(
    @SerializedName("current_password")
    val currentPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)

data class PasswordVerifyRequest(
    @SerializedName("user_pw")
    val userPw: String
)