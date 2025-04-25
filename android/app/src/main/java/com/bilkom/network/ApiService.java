package com.bilkom.network;

import com.bilkom.model.LoginRequest;
import com.bilkom.model.RegistrationRequest;
import com.bilkom.model.AuthResponse;
import com.bilkom.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegistrationRequest registrationRequest);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id, @Header("Authorization") String token);

    @GET("users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email, @Header("Authorization") String token);

    @GET("users/bilkentId/{bilkentId}")
    Call<User> getUserByBilkentId(@Path("bilkentId") String bilkentId, @Header("Authorization") String token);
} 