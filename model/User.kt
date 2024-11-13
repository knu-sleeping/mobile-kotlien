package com.dacslab.android.sleeping.model
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("user_pw")
    val userPw: String?,  // 비밀번호는 실제로는 해시화하여 저장해야 함

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_gender")
    val userGender: String?,  // 'M' 또는 'F' 값

    @SerializedName("user_age")
    val userAge: Int?,

    @SerializedName("user_height")
    val userHeight: Int?,

    @SerializedName("user_weight")
    val userWeight: Int?,

    @SerializedName("user_comp")
    val userComp: Boolean?
)
