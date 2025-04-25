// this is the registration activity class for the registration page
package com.bilkom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bilkom.model.RegistrationRequest;
import com.bilkom.model.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText bilkentIdInput;
    private TextInputEditText phoneNumberInput;
    private TextInputEditText bloodTypeInput;
    private MaterialButton registerButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://your-backend-url/api/") // Replace with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Initialize views
        initializeViews();

        // Set click listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    performRegistration();
                }
            }
        });

        findViewById(R.id.loginText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
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

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            isValid = false;
        } else if (!email.endsWith("@bilkent.edu.tr") && !email.endsWith("@ug.bilkent.edu.tr")) {
            emailInput.setError("Only Bilkent University emails are allowed");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters");
            isValid = false;
        }

        // Validate first name
        if (TextUtils.isEmpty(firstName)) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }

        // Validate last name
        if (TextUtils.isEmpty(lastName)) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }

        // Validate Bilkent ID
        if (TextUtils.isEmpty(bilkentId)) {
            bilkentIdInput.setError("Bilkent ID is required");
            isValid = false;
        } else if (!bilkentId.matches("\\d+")) {
            bilkentIdInput.setError("Bilkent ID must contain only numbers");
            isValid = false;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Phone number is required");
            isValid = false;
        }

        // Validate blood type
        if (TextUtils.isEmpty(bloodType)) {
            bloodTypeInput.setError("Blood type is required");
            isValid = false;
        }

        return isValid;
    }

    private void performRegistration() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String bilkentId = bilkentIdInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String bloodType = bloodTypeInput.getText().toString().trim();

        RegistrationRequest request = new RegistrationRequest(
            email, password, firstName, lastName, bilkentId, phoneNumber, bloodType
        );

        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        Toast.makeText(RegistrationActivity.this, 
                            "Registration successful. Please check your email to verify your account.", 
                            Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, 
                            authResponse.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, 
                        "Registration failed. Please try again.", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, 
                    "Network error. Please check your connection.", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 