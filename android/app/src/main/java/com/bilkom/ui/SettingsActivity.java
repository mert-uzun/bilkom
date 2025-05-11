package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import com.bilkom.model.User;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.button.MaterialButton;
import retrofit2.*;
/**
 * SettingsActivity handles user settings such as updating passwords and logging out.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
public class SettingsActivity extends BaseActivity {
    private EditText newPasswordEdit, confirmPasswordEdit;
    private MaterialButton updatePasswordButton, logoutButton, changeProfilePicButton;
    private ImageView profileImageView;
    private SecureStorage secureStorage;
    private Long userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        getLayoutInflater().inflate(R.layout.activity_settings, findViewById(R.id.contentFrame));

        secureStorage = new SecureStorage(this);
        userId = secureStorage.getUserId();
        token = secureStorage.getAuthToken();

        newPasswordEdit = findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeProfilePicButton = findViewById(R.id.changeProfilePicButton);
        profileImageView = findViewById(R.id.profileImageView);

        updatePasswordButton.setOnClickListener(v -> updatePassword());
        logoutButton.setOnClickListener(v -> logout());
        changeProfilePicButton.setOnClickListener(v -> Toast.makeText(this, "Profile picture change not implemented", Toast.LENGTH_SHORT).show());
    }

    private void updatePassword() {
        String newPassword = newPasswordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            newPasswordEdit.setError("Required");
            confirmPasswordEdit.setError("Required");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEdit.setError("Passwords do not match");
            return;
        }
        if (newPassword.length() < 8) {
            newPasswordEdit.setError("Password must be at least 8 characters");
            return;
        }
        User updatedUser = new User();
        updatedUser.setPasswordHash(newPassword); // Assuming backend expects passwordHash
        RetrofitClient.getInstance().getApiService().updateUser(userId, updatedUser, token)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void logout() {
        secureStorage.clearAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}