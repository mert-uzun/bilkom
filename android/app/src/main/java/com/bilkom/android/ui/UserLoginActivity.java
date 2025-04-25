package com.bilkom.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.android.R;
import com.bilkom.android.network.ApiClient;
import com.bilkom.android.network.AuthService;
import com.bilkom.android.network.models.LoginRequest;
import com.bilkom.android.network.models.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        findViewById(R.id.tvGoRegister).setOnClickListener(v ->
            startActivity(new Intent(this, UserRegisterActivity.class))
        );

        authService = ApiClient.getClient().create(AuthService.class);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            authService.login(new LoginRequest(email, pass))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        AuthResponse body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            // Save token and navigate
                            getSharedPreferences("app_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("jwt_token", body.getToken())
                                .apply();
                            startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String msg = (body != null ? body.getMessage() : "");
                            Toast.makeText(UserLoginActivity.this, "Login failed: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(UserLoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }
}