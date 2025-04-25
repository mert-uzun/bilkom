package com.bilkom.android.network;

import com.bilkom.android.network.models.AuthResponse;
import com.bilkom.android.network.models.LoginRequest;
import com.bilkom.android.network.models.RegisterRequest;
import com.bilkom.android.network.models.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
