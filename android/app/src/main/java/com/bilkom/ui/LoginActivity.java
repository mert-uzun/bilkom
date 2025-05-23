// this is the login activity class for the login page
// it is used to store the login page content
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bilkom.R;
import com.bilkom.model.AuthResponse;
import com.bilkom.model.LoginRequest;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * LoginActivity handles the login functionality for the application.
 * It validates user inputs, communicates with the API for authentication,
 * and navigates to the main screen upon successful login.
 * 
 * @author : Sıla Bozkurt
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private TextView registerText;
    private TextView forgotPasswordText;
    private SecureStorage secureStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        secureStorage = new SecureStorage(this);
        
        // Check if user is already logged in
        if (!secureStorage.getAuthToken().isEmpty()) {
            navigateToMainActivity();
            return;
        }

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Set click listeners
        loginButton.setOnClickListener(v -> loginUser());

        // Set up register and forgot password text views
        registerText.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginActivity.this, Class.forName("com.bilkom.ui.RegistrationActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Registration not available", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RegistrationActivity not found", e);
            }
        });
        
        forgotPasswordText.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);
        
        final TextInputEditText emailInput = dialogView.findViewById(R.id.forgotPasswordEmailInput);
        
        // Set up the buttons
        builder.setPositiveButton("Send Reset Link", null); // Set to null to prevent auto-dismiss
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Override the positive button click to validate before dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            
            // Validate email
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                return;
            }
            
            // Send password reset request
            requestPasswordReset(email);
            dialog.dismiss();
        });
    }
    
    private void requestPasswordReset(String email) {
        // Create request payload
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        
        // Show loading
        Toast.makeText(this, "Sending password reset email...", Toast.LENGTH_SHORT).show();
        
        // Make API call
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.requestPasswordReset(payload).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, 
                        "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        // Handle error
                        String errorMessage = "Failed to send reset link";
                        
                        if (response.errorBody() != null) {
                            try {
                                // Simple extraction of message from JSON
                                errorMessage = response.errorBody().string();
                                if (errorMessage.contains("\"message\":")) {
                                    errorMessage = errorMessage.split("\"message\":")[1];
                                    errorMessage = errorMessage.split("\"")[1];
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body", e);
                                errorMessage = "Failed to send reset link: " + response.code();
                            }
                        }
                        
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling password reset failure", e);
                        Toast.makeText(LoginActivity.this, 
                            "Failed to send reset link", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error during password reset request", t);
                Toast.makeText(
                    LoginActivity.this,
                    "Network error: " + t.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Clear previous errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Validate inputs
        boolean isValid = true;
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        }
        
        if (!isValid) return;

        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText(R.string.logging_in);

        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Make API call
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText(R.string.login);

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    
                    // Check for token - more reliable than isSuccess method
                    if (auth.getToken() != null && !auth.getToken().isEmpty()) {
                        // Save token and user info
                        secureStorage.saveAuthToken(auth.getToken());
                        try {
                            secureStorage.saveUserId(auth.getUserId());
                        } catch (Exception e) {
                            Log.w(TAG, "Unable to save user ID", e);
                        }
                        navigateToMainActivity();
                    } else {
                        // Try different methods if getMessage doesn't exist
                        String message = "Login failed";
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // Handle error conversion error gracefully
                        String errorMessage = "Login failed";
                        
                        if (response.errorBody() != null) {
                            try {
                                // Use direct JSON parsing if converter is not available
                                errorMessage = response.errorBody().string();
                                // Simple extraction of message from JSON
                                if (errorMessage.contains("\"message\":")) {
                                    errorMessage = errorMessage.split("\"message\":")[1];
                                    errorMessage = errorMessage.split("\"")[1];
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body", e);
                                errorMessage = "Login failed: " + response.code();
                            }
                        }
                        
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling login failure", e);
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText(R.string.login);
                Log.e(TAG, "Network error during login", t);
                Toast.makeText(
                    LoginActivity.this,
                    "Network error: " + t.getMessage(),
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void navigateToMainActivity() {
        try {
            // Use reflection to handle potential missing MainActivity
            Intent intent = new Intent(this, Class.forName("com.bilkom.ui.MainActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "MainActivity not found", e);
            // Fallback to home screen
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        }
    }
}