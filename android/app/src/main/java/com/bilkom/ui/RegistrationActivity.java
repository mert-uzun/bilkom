/**
 * This is the registration activity class for the registration page.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.R;
import com.bilkom.model.AuthResponse;
import com.bilkom.model.RegistrationRequest;
import com.bilkom.network.RetrofitClient;
import com.bilkom.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput, firstNameInput, lastNameInput;
    private TextInputEditText bilkentIdInput, phoneNumberInput, bloodTypeInput;
    private MaterialButton registerButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        apiService = RetrofitClient.getInstance().getApiService();
        initializeViews();

        registerButton.setOnClickListener(v -> {
            if (validateInputs()) performRegistration();
        });
        findViewById(R.id.loginText).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        bilkentIdInput = findViewById(R.id.bilkentIdInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        bloodTypeInput = findViewById(R.id.bloodTypeInput);
        registerButton = findViewById(R.id.registerButton);
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String bilkentId = bilkentIdInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String bloodType = bloodTypeInput.getText().toString().trim();

        // Email validation
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            || !(email.endsWith("@bilkent.edu.tr") || email.endsWith("@ug.bilkent.edu.tr"))) {
            emailInput.setError("Enter a valid Bilkent email");
            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters");
            isValid = false;
        }

        // First name validation
        if (TextUtils.isEmpty(firstName)) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }

        // Last name validation
        if (TextUtils.isEmpty(lastName)) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }

        // Bilkent ID validation
        if (TextUtils.isEmpty(bilkentId) || !bilkentId.matches("^\\d+$")) {
            bilkentIdInput.setError("Enter a valid Bilkent ID (numbers only)");
            isValid = false;
        }

        // Phone validation
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("^[+]?\\d{10,15}$")) {
            phoneNumberInput.setError("Enter a valid phone number (10-15 digits with optional + prefix)");
            isValid = false;
        }

        // Blood type validation
        if (TextUtils.isEmpty(bloodType) || !bloodType.matches("^(A|B|AB|0)[+-]$")) {
            bloodTypeInput.setError("Enter a valid blood type (A+, A-, B+, B-, AB+, AB-, 0+, 0-)");
            isValid = false;
        }

        return isValid;
    }

    private void performRegistration() {
        registerButton.setEnabled(false);
        
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String bilkentId = bilkentIdInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String bloodType = bloodTypeInput.getText().toString().trim();
        
        RegistrationRequest req = new RegistrationRequest(
            firstName,
            lastName,
            email, 
            password,
            bilkentId,
            phoneNumber,
            bloodType
        );

        apiService.register(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                registerButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                    Toast.makeText(RegistrationActivity.this,
                        "Registration successful. Check your email to verify.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String errorMessage = "Registration failed";
                    if (response.code() == 409) {
                        errorMessage = "Email already registered";
                    } else if (response.code() == 400) {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                if (errorBody.contains("message")) {
                                    errorMessage = new Gson().fromJson(errorBody, JsonObject.class)
                                        .get("message").getAsString();
                                }
                            }
                        } catch (Exception e) {
                            errorMessage = "Invalid input data";
                        }
                    } else if (response.code() == 500) {
                        errorMessage = "Server error. Please try again later.";
                    }
                    Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                String errorMessage = "Network error. Please check your connection and try again.";
                if (t instanceof IOException) {
                    errorMessage = "No internet connection. Please try again when online.";
                }
                Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
