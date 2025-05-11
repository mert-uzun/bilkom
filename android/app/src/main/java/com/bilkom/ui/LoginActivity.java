// this is the login activity class for the login page
// it is used to store the login page content
package com.bilkom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.R;
import com.bilkom.model.AuthResponse;
import com.bilkom.model.LoginRequest;
import com.bilkom.network.RetrofitClient;
import com.bilkom.network.ApiService;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity handles the login functionality for the application.
 * It validates user inputs, communicates with the API for authentication,
 * and navigates to the main screen upon successful login.
 * 
 * @author : Sıla Bozkurt
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailLayout, passwordLayout;
    private MaterialButton loginButton;
    private SecureStorage secureStorage;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();

        if (secureStorage.getAuthToken() != null) {
            navigateToMain();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) performLogin();
        });
        findViewById(R.id.registerText).setOnClickListener(v ->
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class))
        );
        findViewById(R.id.forgotPasswordText).setOnClickListener(v ->
            Toast.makeText(LoginActivity.this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            || !(email.endsWith("@bilkent.edu.tr") || email.endsWith("@ug.bilkent.edu.tr"))) {
            emailLayout.setError("Enter a valid Bilkent email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }

    private void performLogin() {
        loginButton.setEnabled(false);
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                loginButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    if (auth.isSuccess()) {
                        secureStorage.saveAuthToken("Bearer " + auth.getToken());
                        secureStorage.saveUserId(auth.getUserId());
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, auth.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error. Check connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}