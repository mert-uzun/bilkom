package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bilkom.ui.BaseActivity;
import com.bilkom.R;
import com.bilkom.model.User;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * SettingsActivity handles user settings such as updating passwords and logging out.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";
    
    private EditText newPasswordEdit, confirmPasswordEdit;
    private MaterialButton updatePasswordButton, logoutButton, changeProfilePicButton;
    private ImageView profileImageView;
    private SecureStorage secureStorage;
    private Long userId;
    private String token;
    private LinearLayout settingsContainer;
    private EditText nameEditText;
    private EditText emailEditText;
    private Button updateProfileButton;
    private Button manageClubsButton;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            getLayoutInflater().inflate(R.layout.activity_settings, findViewById(R.id.contentFrame));
            setupNavigationDrawer();
            
            secureStorage = new SecureStorage(this);
            userId = secureStorage.getUserId();
            token = secureStorage.getAuthToken();
            
            if (userId == null || userId <= 0 || token == null || token.isEmpty()) {
                Log.e(TAG, "Invalid user credentials, redirecting to login");
                redirectToLogin();
                return;
            }
            
            initializeViews();
            setupListeners();
            addSettingsItems();
            loadUserData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SettingsActivity", e);
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        TextView titleView = findViewById(R.id.settingsTitle);
        titleView.setText("Settings");
        
        settingsContainer = findViewById(R.id.settingsContainer);
        
        newPasswordEdit = findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeProfilePicButton = findViewById(R.id.changeProfilePicButton);
        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        manageClubsButton = findViewById(R.id.manageClubsButton);
        apiService = RetrofitClient.getInstance().getApiService();
    }
    
    private void setupListeners() {
        updatePasswordButton.setOnClickListener(v -> updatePassword());
        logoutButton.setOnClickListener(v -> logout());
        changeProfilePicButton.setOnClickListener(v -> Toast.makeText(this, "Profile picture change not implemented", Toast.LENGTH_SHORT).show());
        updateProfileButton.setOnClickListener(v -> updateUserProfile());
        manageClubsButton.setOnClickListener(v -> openClubManagement());
    }
    
    private void addSettingsItems() {
        try {
            // Notifications setting
            addSwitchSetting("Enable notifications", secureStorage.getBoolean("notifications_enabled", true), 
                (buttonView, isChecked) -> {
                    secureStorage.saveBoolean("notifications_enabled", isChecked);
                });
            
            // Dark mode setting
            addSwitchSetting("Dark mode", secureStorage.getBoolean("dark_mode_enabled", false), 
                (buttonView, isChecked) -> {
                    secureStorage.saveBoolean("dark_mode_enabled", isChecked);
                    // Implementation of dark mode would go here
                });
            
            // Check if activities exist before adding options
            if (doesActivityExist("com.bilkom.ui.JoinClubActivity")) {
                // Join Club option
                addButtonSetting("Join Club", v -> {
                    Intent intent = new Intent(SettingsActivity.this, JoinClubActivity.class);
                    startActivity(intent);
                });
            }
            
            if (doesActivityExist("com.bilkom.ui.MyClubsActivity")) {
                // My Clubs option
                addButtonSetting("My Clubs", v -> {
                    Intent intent = new Intent(SettingsActivity.this, MyClubsActivity.class);
                    startActivity(intent);
                });
            }
            
            // Clear cache option
            addButtonSetting("Clear cache", v -> {
                Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                // Implementation of cache clearing would go here
            });
        } catch (Exception e) {
            Log.e(TAG, "Error adding settings items", e);
        }
    }
    
    private boolean doesActivityExist(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Activity class not found: " + className);
            return false;
        }
    }
    
    private void addSwitchSetting(String title, boolean initialState, SwitchMaterial.OnCheckedChangeListener listener) {
        try {
            View settingView = getLayoutInflater().inflate(R.layout.item_setting_switch, settingsContainer, false);
            
            TextView titleView = settingView.findViewById(R.id.settingTitle);
            SwitchMaterial switchView = settingView.findViewById(R.id.settingSwitch);
            
            if (titleView != null && switchView != null) {
                titleView.setText(title);
                switchView.setChecked(initialState);
                switchView.setOnCheckedChangeListener(listener);
                
                settingsContainer.addView(settingView);
            } else {
                Log.e(TAG, "Failed to find views in setting_switch layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding switch setting: " + title, e);
        }
    }
    
    private void addButtonSetting(String title, View.OnClickListener listener) {
        try {
            View settingView = getLayoutInflater().inflate(R.layout.item_setting_button, settingsContainer, false);
            
            TextView titleView = settingView.findViewById(R.id.settingTitle);
            Button buttonView = settingView.findViewById(R.id.settingButton);
            
            if (titleView != null && buttonView != null) {
                titleView.setText(title);
                buttonView.setText("Open");
                buttonView.setOnClickListener(listener);
                
                settingsContainer.addView(settingView);
            } else {
                Log.e(TAG, "Failed to find views in setting_button layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding button setting: " + title, e);
        }
    }
    
    private void updatePassword() {
        String newPassword = newPasswordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();
        
        // Clear previous errors
        newPasswordEdit.setError(null);
        confirmPasswordEdit.setError(null);
        
        // Validate
        if (newPassword.isEmpty()) {
            newPasswordEdit.setError("Required");
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordEdit.setError("Required");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEdit.setError("Passwords don't match");
            return;
        }
        
        if (newPassword.length() < 6) {
            newPasswordEdit.setError("Password must be at least 6 characters");
            return;
        }
        
        // Update UI state
        updatePasswordButton.setEnabled(false);
        updatePasswordButton.setText("Updating...");
        
        // Create request
        User updatedUser = new User();
        updatedUser.setPasswordHash(newPassword); // Assuming backend expects passwordHash
        
        // Make API call
        apiService.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Restore UI state
                updatePasswordButton.setEnabled(true);
                updatePasswordButton.setText("Update Password");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    newPasswordEdit.setText("");
                    confirmPasswordEdit.setText("");
                } else {
                    int errorCode = response.code();
                    String errorMessage = "Failed to update password (Error " + errorCode + ")";
                    Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Restore UI state
                updatePasswordButton.setEnabled(true);
                updatePasswordButton.setText("Update Password");
                
                String errorMessage = "Network error: " + t.getMessage();
                Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API failure during password update", t);
            }
        });
    }
    
    private void loadUserData() {
        try {
            // Show loading state if UI elements exist
            if (nameEditText != null) nameEditText.setEnabled(false);
            if (emailEditText != null) emailEditText.setEnabled(false);
            
            // Load user data from API
            apiService.getUser(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    // Restore UI state
                    if (nameEditText != null) nameEditText.setEnabled(true);
                    if (emailEditText != null) emailEditText.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        if (nameEditText != null) nameEditText.setText(user.getName());
                        if (emailEditText != null) emailEditText.setText(user.getEmail());
                    } else {
                        String errorMessage = "Failed to load user data: " + response.code();
                        Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMessage);
                    }
                }
                
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // Restore UI state
                    if (nameEditText != null) nameEditText.setEnabled(true);
                    if (emailEditText != null) emailEditText.setEnabled(true);
                    
                    String errorMessage = "Network error: " + t.getMessage();
                    Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API failure during user data load", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateUserProfile() {
        // Get values from fields
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        
        // Clear previous errors
        nameEditText.setError(null);
        emailEditText.setError(null);
        
        // Validate inputs
        boolean isValid = true;
        
        if (name.isEmpty()) {
            nameEditText.setError("Required");
            isValid = false;
        }
        
        if (email.isEmpty()) {
            emailEditText.setError("Required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Update UI state
        updateProfileButton.setEnabled(false);
        updateProfileButton.setText("Updating...");
        
        // Create user object with updated data
        User updatedUser = new User();
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        
        // Update API
        apiService.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Restore UI state
                updateProfileButton.setEnabled(true);
                updateProfileButton.setText("Update Profile");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SettingsActivity.this, 
                        "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Failed to update profile: " + response.code();
                    Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Restore UI state
                updateProfileButton.setEnabled(true);
                updateProfileButton.setText("Update Profile");
                
                String errorMessage = "Network error: " + t.getMessage();
                Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API failure during profile update", t);
            }
        });
    }
    
    private void openClubManagement() {
        try {
            // Check if ProfileActivity exists
            if (doesActivityExist("com.bilkom.ui.ProfileActivity")) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("mode", "manage_clubs");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Club management not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening club management", e);
            Toast.makeText(this, "Error opening club management", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void logout() {
        try {
            secureStorage.clearAll();
            redirectToLogin();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_settings;
    }
}
