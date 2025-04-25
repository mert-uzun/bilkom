package com.bilkom.android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bilkom.android.R;
import com.bilkom.android.network.AuthRepository;
import com.bilkom.android.network.NetworkModule;
import com.bilkom.android.network.models.LoginResponse;
import com.bilkom.android.ui.viewmodel.LoginViewModel;

public class UserLoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private LoginViewModel loginViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        
        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        
        // Initialize ViewModel with the actual AuthApiService
        AuthRepository authRepository = new AuthRepository(NetworkModule.getAuthApiService());
        loginViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new LoginViewModel(authRepository);
            }
        }).get(LoginViewModel.class);
        
        // Set up click listeners
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (validateInput(email, password)) {
                loginViewModel.login(email, password);
            }
        });
        
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserRegisterActivity.class));
        });
        
        // Observe ViewModel
        loginViewModel.getLoginResponse().observe(this, this::handleLoginResponse);
        loginViewModel.getError().observe(this, this::handleError);
    }
    
    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }
    
    private void handleLoginResponse(LoginResponse response) {
        if (response.isSuccess()) {
            // Save token and user ID to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("BilkomPrefs", MODE_PRIVATE);
            prefs.edit()
                .putString("token", response.getToken())
                .putLong("userId", response.getUserId())
                .apply();
            
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