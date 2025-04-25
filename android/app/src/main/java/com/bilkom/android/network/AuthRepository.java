package com.bilkom.android.network;

import com.bilkom.android.network.models.LoginRequest;
import com.bilkom.android.network.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApiService authApiService;
    
    public AuthRepository(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }
    
    public void login(String email, String password, final AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        authApiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login failed");
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public interface AuthCallback {
        void onSuccess(LoginResponse response);
        void onError(String error);
    }
} 