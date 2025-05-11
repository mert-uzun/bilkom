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
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String bloodType = bloodTypeInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            || !(email.endsWith("@bilkent.edu.tr") || email.endsWith("@ug.bilkent.edu.tr"))) {
            emailInput.setError("Enter a valid Bilkent email");
            isValid = false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters");
            isValid = false;
        }
        if (TextUtils.isEmpty(firstName)) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(bilkentId) || !bilkentId.matches("\\d+")) {
            bilkentIdInput.setError("Enter a valid Bilkent ID");
            isValid = false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Phone number is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(bloodType)) {
            bloodTypeInput.setError("Blood type is required");
            isValid = false;
        }
        return isValid;
    }

    private void performRegistration() {
        registerButton.setEnabled(false);
        RegistrationRequest req = new RegistrationRequest(
            emailInput.getText().toString().trim(),
            passwordInput.getText().toString().trim(),
            firstNameInput.getText().toString().trim(),
            lastNameInput.getText().toString().trim(),
            bilkentIdInput.getText().toString().trim(),
            phoneNumberInput.getText().toString().trim(),
            bloodTypeInput.getText().toString().trim()
        );

        apiService.register(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                registerButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegistrationActivity.this,
                        "Registration successful. Check your email to verify.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                Toast.makeText(RegistrationActivity.this, "Network error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
