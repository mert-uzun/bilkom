package com.bilkom.android.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.android.R;
import com.bilkom.android.network.ApiClient;
import com.bilkom.android.network.AuthService;
import com.bilkom.android.network.models.RegistrationRequest;
import com.bilkom.android.network.models.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {
    private TextInputEditText etFirstName, etLastName, etEmail,
            etPassword, etBilkentId, etPhone, etBloodType;
    private MaterialButton btnRegister;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etBilkentId = findViewById(R.id.etBilkentId);
        etPhone = findViewById(R.id.etPhone);
        etBloodType = findViewById(R.id.etBloodType);
        btnRegister = findViewById(R.id.btnRegister);
        findViewById(R.id.tvGoLogin).setOnClickListener(v -> finish());

        authService = ApiClient.getClient().create(AuthService.class);

        btnRegister.setOnClickListener(v -> {
            String fName = etFirstName.getText().toString().trim();
            String lName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String id = etBilkentId.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String blood = etBloodType.getText().toString().trim();
            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty()
                    || id.isEmpty() || phone.isEmpty() || blood.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            RegistrationRequest req = new RegistrationRequest(
                    email, pass, fName, lName, id, phone, blood
            );
            authService.register(req)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        AuthResponse body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            Toast.makeText(UserRegisterActivity.this, "Registered! Please log in.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = (body != null ? body.getMessage() : "");
                            Toast.makeText(UserRegisterActivity.this, "Registration failed: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(UserRegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }
}
