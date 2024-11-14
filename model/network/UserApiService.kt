package com.dacslab.android.sleeping.model.network

import com.dacslab.android.sleeping.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApiService {
    @GET("/user/info")
    suspend fun userInfoAPI(): Response<User>

    @PATCH("/user/password")
    suspend fun passwordChangeAPI(@Body request: PasswordChangeRequest) : Response<BaseResponseImpl>

    @PATCH("/user/update")
    suspend fun updateUserInfoAPI(@Body request: User): Response<BaseResponseImpl>

    @DELETE("/user/delete")
    suspend fun deleteAccountAPI(): Response<BaseResponseImpl>

    @POST("/user/pw_verify")
    suspend fun verifyPasswordAPI(@Body request: PasswordVerifyRequest) : Response<BaseResponseImpl>
}