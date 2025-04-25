package com.bilkom.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bilkom.android.R;
import com.bilkom.android.network.AuthRepository;
import com.bilkom.android.network.models.LoginResponse;
import com.bilkom.android.ui.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private LoginViewModel loginViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        
        // Initialize ViewModel
        AuthRepository authRepository = new AuthRepository(/* TODO: Initialize with Retrofit instance */);
        loginViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new LoginViewModel(authRepository);
            }
        }).get(LoginViewModel.class);
        
        // Set up click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginViewModel.login(email, password);
            }
        });
        
        // Observe ViewModel
        loginViewModel.getLoginResponse().observe(this, LoginActivity.this::handleLoginResponse);
        loginViewModel.getError().observe(this, LoginActivity.this::handleError);
    }
    
    private void handleLoginResponse(LoginResponse response) {
        if (response.isSuccess()) {
            // Save token and user ID
            // TODO: Save to SharedPreferences or other storage
            
            // Navigate to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
} 