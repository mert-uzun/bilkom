package com.bilkom.network;

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

    private static final String BASE_URL = "http://10.0.2.2:8080/api/"; //localhost
    private static volatile Retrofit retrofit;
    private static volatile ApiService apiService;

    private RetrofitClient() { }

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
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    apiService = getRetrofit().create(ApiService.class);
                }
            }
        }
        return apiService;
    }
}