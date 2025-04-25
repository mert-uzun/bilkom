package com.bilkom.android.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {
    // The backend is running on port 8080 with context path /api
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // For Android Emulator
    // private static final String BASE_URL = "http://localhost:8080/"; // For physical device
    
    public static Retrofit getRetrofitInstance() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
            
        return new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
    
    public static AuthApiService getAuthApiService() {
        return getRetrofitInstance().create(AuthApiService.class);
    }
} 