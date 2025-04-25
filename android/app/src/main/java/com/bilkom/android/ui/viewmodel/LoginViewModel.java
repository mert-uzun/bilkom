package com.bilkom.android.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.bilkom.android.network.AuthRepository;
import com.bilkom.android.network.models.LoginResponse;

public class LoginViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    
    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    public void login(String email, String password) {
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                loginResponse.postValue(response);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }
} 