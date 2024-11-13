// AuthRepository.kt
package com.dacslab.android.sleeping.model.repository

import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.source.local.Token


abstract class AuthRepository {

    abstract suspend fun isUserAuthenticated(): Boolean

    abstract suspend fun register(user: User): Pair<Boolean, String>

    abstract suspend fun login(userId: String, userPw: String): Pair<Boolean, String>

    abstract suspend fun logout()

    abstract suspend fun getUserToken(): Token?

    abstract suspend fun checkToken()
}
