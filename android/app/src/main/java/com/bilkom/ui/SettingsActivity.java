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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity {
    private EditText emailEdit, firstNameEdit, lastNameEdit, bilkentIdEdit, phoneEdit, bloodTypeEdit;
    private EditText newPasswordEdit, confirmPasswordEdit;
    private MaterialButton saveButton, updateUsernameButton, updatePasswordButton, logoutButton, changeProfilePicButton;
    private ImageView profileImageView;
    private SecureStorage secureStorage;
    private Long userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        
        // Inflate the settings content into the content frame
        getLayoutInflater().inflate(R.layout.activity_settings, findViewById(R.id.contentFrame));

        secureStorage = new SecureStorage(this);
        userId = secureStorage.getUserId();
        token = secureStorage.getAuthToken();

        emailEdit = findViewById(R.id.settingsEmail);
        firstNameEdit = findViewById(R.id.settingsFirstName);
        lastNameEdit = findViewById(R.id.settingsLastName);
        bilkentIdEdit = findViewById(R.id.settingsBilkentId);
        phoneEdit = findViewById(R.id.settingsPhone);
        bloodTypeEdit = findViewById(R.id.settingsBloodType);
        saveButton = findViewById(R.id.saveSettingsButton);
        newPasswordEdit = findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        updateUsernameButton = findViewById(R.id.updateUsernameButton);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeProfilePicButton = findViewById(R.id.changeProfilePicButton);
        profileImageView = findViewById(R.id.profileImageView);

        loadUserProfile();
        saveButton.setOnClickListener(v -> updateUserProfile());
        updatePasswordButton.setOnClickListener(v -> updatePassword());
        logoutButton.setOnClickListener(v -> logout());
        changeProfilePicButton.setOnClickListener(v -> Toast.makeText(this, "Profile picture change not implemented", Toast.LENGTH_SHORT).show());
    }

    private void loadUserProfile() {
        if (userId == null || token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitClient.getInstance().getApiService().getUserById(userId, token)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        emailEdit.setText(user.getEmail());
                        firstNameEdit.setText(user.getFirstName());
                        lastNameEdit.setText(user.getLastName());
                        bilkentIdEdit.setText(user.getBilkentId());
                        phoneEdit.setText(user.getPhoneNumber());
                        bloodTypeEdit.setText(user.getBloodType());
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateUserProfile() {
        User updatedUser = new User();
        updatedUser.setEmail(emailEdit.getText().toString().trim());
        updatedUser.setFirstName(firstNameEdit.getText().toString().trim());
        updatedUser.setLastName(lastNameEdit.getText().toString().trim());
        updatedUser.setBilkentId(bilkentIdEdit.getText().toString().trim());
        updatedUser.setPhoneNumber(phoneEdit.getText().toString().trim());
        updatedUser.setBloodType(bloodTypeEdit.getText().toString().trim());

        RetrofitClient.getInstance().getApiService().updateUser(userId, updatedUser, token)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateUsername() {
        String newUsername = newUsernameEdit.getText().toString().trim();
        if (newUsername.isEmpty()) {
            newUsernameEdit.setError("Username cannot be empty");
            return;
        }
        User updatedUser = new User();
        updatedUser.setFirstName(newUsername); // Assuming username is firstName, adjust if needed
        RetrofitClient.getInstance().getApiService().updateUser(userId, updatedUser, token)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
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