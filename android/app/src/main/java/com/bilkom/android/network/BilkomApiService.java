package com.bilkom.android.network;

import com.bilkom.android.network.models.LoginRequest;
import com.bilkom.android.network.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BilkomApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
} 