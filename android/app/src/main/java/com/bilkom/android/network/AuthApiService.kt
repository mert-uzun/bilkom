package com.bilkom.android.network

import com.bilkom.android.network.models.LoginRequest
import com.bilkom.android.network.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
} 