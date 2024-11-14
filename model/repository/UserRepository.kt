package com.dacslab.android.sleeping.model.repository

import com.dacslab.android.sleeping.model.User

abstract class UserRepository {

    abstract suspend fun getUserInfo(): Triple<Boolean, User?, String?>

    abstract suspend fun passwordChange(
        currentPassword: String,
        newPassword: String
    ): Pair<Boolean, String>

    abstract suspend fun updateUserInfo(user: User): Pair<Boolean, String?>
}