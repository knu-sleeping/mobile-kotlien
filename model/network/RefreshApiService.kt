package com.dacslab.android.sleeping.model.network

import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface RefreshApiService {
    @POST("/auth/refresh")
    suspend fun refreshAPI(
        @Header("Authorization") refreshToken: String
    ): Response<RefreshResponse>
}