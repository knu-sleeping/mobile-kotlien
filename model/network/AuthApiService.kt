package com.dacslab.android.sleeping.model.network

import com.dacslab.android.sleeping.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface AuthApiService {
    @POST("/auth/login")
    suspend fun loginAPI(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun registerAPI(@Body request: User): Response<RegisterResponse>

    @GET("/auth/check")
    suspend fun tokenCheckAPI(): Response<BaseResponseImpl>
}

