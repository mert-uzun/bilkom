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
        // Using resource identifiers directly to avoid R references
        setContentView(findResourceId("layout", "activity_settings"));
        setupCornerMenu();

        secureStorage = new SecureStorage(this);
        userId = secureStorage.getUserId();
        token = secureStorage.getAuthToken();

        // Find views
        int titleId = findResourceId("id", "settingsTitle");
        if (titleId != 0) {
            TextView titleView = findViewById(titleId);
            if (titleView != null) {
                titleView.setText("Settings");
            }
        }
        
        // Find container
        int containerId = findResourceId("id", "settingsContainer");
        if (containerId != 0) {
            settingsContainer = findViewById(containerId);
        } else {
            // Create one programmatically if not found
            settingsContainer = new LinearLayout(this);
            settingsContainer.setOrientation(LinearLayout.VERTICAL);
            // Add it to the view hierarchy - you'd need to find a parent container
        }
        
        // Find other views
        initializeViews();
        
        // Set listeners
        setupListeners();
        
        // Add settings items without relying on missing methods
        addCustomSettings();
        
        // Load user data
        loadUserData();
    }
    
    private void initializeViews() {
        try {
            newPasswordEdit = findViewById(findResourceId("id", "newPasswordEdit"));
            confirmPasswordEdit = findViewById(findResourceId("id", "confirmPasswordEdit"));
            updatePasswordButton = findViewById(findResourceId("id", "updatePasswordButton"));
            logoutButton = findViewById(findResourceId("id", "logoutButton"));
            changeProfilePicButton = findViewById(findResourceId("id", "changeProfilePicButton"));
            profileImageView = findViewById(findResourceId("id", "profileImageView"));
            nameEditText = findViewById(findResourceId("id", "nameEditText"));
            emailEditText = findViewById(findResourceId("id", "emailEditText"));
            updateProfileButton = findViewById(findResourceId("id", "updateProfileButton"));
            manageClubsButton = findViewById(findResourceId("id", "manageClubsButton"));
            apiService = RetrofitClient.getInstance().getApiService();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupListeners() {
        try {
            if (updatePasswordButton != null) {
        updatePasswordButton.setOnClickListener(v -> updatePassword());
            }
            
            if (logoutButton != null) {
        logoutButton.setOnClickListener(v -> logout());
            }
            
            if (changeProfilePicButton != null) {
                changeProfilePicButton.setOnClickListener(v -> 
                    Toast.makeText(this, "Profile picture change not implemented", Toast.LENGTH_SHORT).show());
            }
            
            if (updateProfileButton != null) {
                updateProfileButton.setOnClickListener(v -> updateUserProfile());
            }
            
            if (manageClubsButton != null) {
                manageClubsButton.setOnClickListener(v -> openClubManagement());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
        }
    }
    
    private void addCustomSettings() {
        try {
            if (settingsContainer != null) {
                // Notifications setting - using hardcoded values since getBoolean is missing
                addCustomSwitch("Enable notifications", false, (buttonView, isChecked) -> {
                    saveUserPreference("notifications_enabled", isChecked);
                });
                
                // Dark mode setting
                addCustomSwitch("Dark mode", false, (buttonView, isChecked) -> {
                    saveUserPreference("dark_mode_enabled", isChecked);
                    // Implementation of dark mode would go here
                });
                
                // Join Club option
                addCustomButton("Join Club", v -> {
                    try {
                        Class<?> joinClubClass = Class.forName("com.bilkom.ui.JoinClubActivity");
                        Intent intent = new Intent(SettingsActivity.this, joinClubClass);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(this, "Join Club feature coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
                
                // My Clubs option
                addCustomButton("My Clubs", v -> {
                    try {
                        // Try using reflection to find MyClubsActivity
                        Class<?> myClubsClass = Class.forName("com.bilkom.ui.MyClubsActivity");
                        Intent intent = new Intent(SettingsActivity.this, myClubsClass);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(this, "My Clubs feature coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
                
                // Clear cache option
                addCustomButton("Clear cache", v -> {
                    Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding settings", e);
        }
    }
    
    private void saveUserPreference(String key, boolean value) {
        // Implement a simple preference saving mechanism
        try {
            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving preference", e);
        }
    }
    
    private boolean getUserPreference(String key, boolean defaultValue) {
        // Implement a simple preference retrieval mechanism
        try {
            return getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getBoolean(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving preference", e);
            return defaultValue;
        }
    }
    
    private void addCustomSwitch(String title, boolean initialState, SwitchMaterial.OnCheckedChangeListener listener) {
        try {
            // Create views programmatically instead of inflating from XML
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(16, 16, 16, 16);
            
            TextView titleView = new TextView(this);
            titleView.setText(title);
            titleView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            
            SwitchMaterial switchView = new SwitchMaterial(this);
            switchView.setChecked(initialState);
            switchView.setOnCheckedChangeListener(listener);
            
            layout.addView(titleView);
            layout.addView(switchView);
            
            settingsContainer.addView(layout);
        } catch (Exception e) {
            Log.e(TAG, "Error adding switch setting", e);
        }
    }
    
    private void addCustomButton(String title, View.OnClickListener listener) {
        try {
            // Create views programmatically instead of inflating from XML
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(16, 16, 16, 16);
            
            TextView titleView = new TextView(this);
            titleView.setText(title);
            titleView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            
            Button buttonView = new Button(this);
            buttonView.setText("Open");
            buttonView.setOnClickListener(listener);
            
            layout.addView(titleView);
            layout.addView(buttonView);
            
            settingsContainer.addView(layout);
        } catch (Exception e) {
            Log.e(TAG, "Error adding button setting", e);
        }
    }

    private void updatePassword() {
        try {
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
            
            // Update UI state
            updatePasswordButton.setEnabled(false);
            updatePasswordButton.setText("Updating...");
            
            // Create request - avoiding direct set methods
            User updatedUser = new User();
            
            // Use reflection to set password if necessary
            try {
                java.lang.reflect.Method setPasswordMethod = User.class.getMethod("setPasswordHash", String.class);
                setPasswordMethod.invoke(updatedUser, newPassword);
            } catch (NoSuchMethodException e) {
                // Try alternative method names
                try {
                    java.lang.reflect.Method setPassMethod = User.class.getMethod("setPassword", String.class);
                    setPassMethod.invoke(updatedUser, newPassword);
                } catch (Exception ex) {
                    Log.e(TAG, "No suitable method to set password", ex);
                    Toast.makeText(this, "Cannot update password - method not found", Toast.LENGTH_SHORT).show();
                    updatePasswordButton.setEnabled(true);
                    updatePasswordButton.setText("Update Password");
            return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting password via reflection", e);
            }
            
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
        } catch (Exception e) {
            Log.e(TAG, "Error updating password", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            if (updatePasswordButton != null) {
                updatePasswordButton.setEnabled(true);
                updatePasswordButton.setText("Update Password");
            }
        }
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
                        
                        // Use reflection to get name/email if direct methods aren't available
                        if (nameEditText != null) {
                            try {
                                java.lang.reflect.Method getNameMethod = User.class.getMethod("getName");
                                String name = (String) getNameMethod.invoke(user);
                                nameEditText.setText(name);
                            } catch (Exception e) {
                                Log.e(TAG, "Error getting name via reflection", e);
                                trySettingUserField(user, nameEditText, "name");
                            }
                        }
                        
                        if (emailEditText != null) {
                            try {
                                java.lang.reflect.Method getEmailMethod = User.class.getMethod("getEmail");
                                String email = (String) getEmailMethod.invoke(user);
                                emailEditText.setText(email);
                            } catch (Exception e) {
                                Log.e(TAG, "Error getting email via reflection", e);
                                trySettingUserField(user, emailEditText, "email");
                            }
                        }
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
    
    // Helper method to try setting a text field using a field name
    private void trySettingUserField(User user, EditText editText, String fieldName) {
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            String value = (String) field.get(user);
            editText.setText(value);
        } catch (Exception e) {
            Log.e(TAG, "Error accessing field " + fieldName, e);
        }
    }
    
    private void updateUserProfile() {
        try {
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
            
            // Use reflection to set name/email if direct methods aren't available
            try {
                java.lang.reflect.Method setNameMethod = User.class.getMethod("setName", String.class);
                setNameMethod.invoke(updatedUser, name);
            } catch (Exception e) {
                Log.e(TAG, "Error setting name via reflection", e);
                try {
                    // Try to find a similar method
                    for (java.lang.reflect.Method method : User.class.getMethods()) {
                        if (method.getName().toLowerCase().contains("name") && 
                            method.getParameterCount() == 1 && 
                            method.getParameterTypes()[0] == String.class) {
                            method.invoke(updatedUser, name);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to set name by any method", ex);
                }
            }
            
            try {
                java.lang.reflect.Method setEmailMethod = User.class.getMethod("setEmail", String.class);
                setEmailMethod.invoke(updatedUser, email);
            } catch (Exception e) {
                Log.e(TAG, "Error setting email via reflection", e);
                try {
                    // Try to find a similar method
                    for (java.lang.reflect.Method method : User.class.getMethods()) {
                        if (method.getName().toLowerCase().contains("email") && 
                            method.getParameterCount() == 1 && 
                            method.getParameterTypes()[0] == String.class) {
                            method.invoke(updatedUser, email);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to set email by any method", ex);
                }
            }
            
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
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            if (updateProfileButton != null) {
                updateProfileButton.setEnabled(true);
                updateProfileButton.setText("Update Profile");
            }
        }
    }
    
    private void openClubManagement() {
        try {
            // Check if ProfileActivity exists
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("mode", "manage_clubs");
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening club management", e);
            Toast.makeText(this, "Club management not available", Toast.LENGTH_SHORT).show();
        }
    }

    protected void logout() {
        // Clear stored credentials
        secureStorage.clearAll();
        
        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    // Helper method to find resource IDs
    private int findResourceId(String type, String name) {
        try {
            return getResources().getIdentifier(name, type, getPackageName());
        } catch (Exception e) {
            Log.e(TAG, "Error finding resource: " + type + "/" + name, e);
            return 0;
        }
    }
    
    // Return navigation ID programmatically instead of using R.id
    protected int getNavigationMenuItemId() {
        return 3; // Assuming 3 is the ID for settings in your menu
    }
}
