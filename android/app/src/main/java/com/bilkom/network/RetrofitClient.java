package com.bilkom.network;

import android.content.Context;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client for API requests
 * 
 * @author Mert Uzun and Sıla Bozkurt
 * @version 1.0
 * @since 2025-05-09
 */
public final class RetrofitClient {
    private static final String BASE_URL = "http://10.201.158.83:8080/api/" ;
    private static volatile Retrofit retrofit;
    private static volatile ApiService apiService;
    private static volatile Context applicationContext;
    private static final Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX") 
        .create();

    private RetrofitClient() { }

    // Add init method to store application context if needed
    public static void init(Context appContext) {
        applicationContext = appContext.getApplicationContext();
    }

    // Singleton holder pattern
    private static class RetrofitClientHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return RetrofitClientHolder.INSTANCE;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient okHttp = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(log)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttp)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    public ApiService getApiService() {
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    apiService = getRetrofit().create(ApiService.class);
                }
            }
        }
        return apiService;
    }
    
    public static Context getApplicationContext() {
        return applicationContext;
    }
}