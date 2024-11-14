package com.dacslab.android.sleeping.model.network

import com.dacslab.android.sleeping.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {
    @GET("/user/info")
    suspend fun userInfoAPI(): Response<User>

    @PATCH("/user/password")
    suspend fun passwordChangeAPI(@Body request: PasswordChangeRequest) : Response<BaseResponseImpl>

    @PATCH("/user/update")
    suspend fun updateUserInfoAPI(@Body request: User): Response<BaseResponseImpl>
}