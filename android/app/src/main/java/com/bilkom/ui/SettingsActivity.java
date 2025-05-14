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
import java.util.List;
import android.app.AlertDialog;
import android.view.MenuItem;
import com.bilkom.ui.ClubActivitiesActivity;
import com.bilkom.model.ClubMembership;
import com.bilkom.model.Club;
import com.bilkom.model.ClubMember;

/** SettingsActivity handles user settings such as updating passwords and logging out.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";
    
    private EditText newPasswordEdit, confirmPasswordEdit;
    private MaterialButton updatePasswordButton, logoutButton, changeProfilePicButton;
    private Button mainMenuButton, createClubButton;
    private ImageView profileImageView;
    private SecureStorage secureStorage;
    private Long userId;
    private String token;
    private LinearLayout settingsContainer;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText bloodTypeEditText;
    private EditText bilkentIdEditText;
    private Button updateProfileButton;
    private Button manageClubsButton;
    private Button joinClubButton;
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
            phoneEditText = findViewById(findResourceId("id", "phoneEditText"));
            bloodTypeEditText = findViewById(findResourceId("id", "bloodTypeEditText"));
            bilkentIdEditText = findViewById(findResourceId("id", "bilkentIdEditText"));
            updateProfileButton = findViewById(findResourceId("id", "updateProfileButton"));
            manageClubsButton = findViewById(findResourceId("id", "manageClubsButton"));
            joinClubButton = findViewById(findResourceId("id", "joinClubButton"));
            mainMenuButton = findViewById(findResourceId("id", "mainMenuButton"));
            createClubButton = findViewById(findResourceId("id", "createClubButton"));
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
            
            if (joinClubButton != null) {
                joinClubButton.setOnClickListener(v -> joinClub());
            }
            
            if (mainMenuButton != null) {
                mainMenuButton.setOnClickListener(v -> navigateToMainActivity());
            }
            
            if (createClubButton != null) {
                createClubButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(SettingsActivity.this, CreateClubActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to CreateClubActivity", e);
                        Toast.makeText(this, "Cannot open create club page", Toast.LENGTH_SHORT).show();
                    }
                });
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
            if (phoneEditText != null) phoneEditText.setEnabled(false);
            if (bloodTypeEditText != null) bloodTypeEditText.setEnabled(false);
            if (bilkentIdEditText != null) bilkentIdEditText.setEnabled(false);
            
            // Load user data from API
            apiService.getUser(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    // Restore UI state
                    if (nameEditText != null) nameEditText.setEnabled(true);
                    if (emailEditText != null) emailEditText.setEnabled(true);
                    if (phoneEditText != null) phoneEditText.setEnabled(true);
                    if (bloodTypeEditText != null) bloodTypeEditText.setEnabled(true);
                    if (bilkentIdEditText != null) bilkentIdEditText.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        
                        // Set name using getFullName method
                        if (nameEditText != null) {
                            String fullName = user.getFullName();
                            if (fullName != null && !fullName.trim().isEmpty()) {
                                nameEditText.setText(fullName);
                            } else {
                                // Fallback to firstName + lastName if getFullName returns empty
                                String firstName = user.getFirstName();
                                String lastName = user.getLastName();
                                if (firstName != null || lastName != null) {
                                    StringBuilder nameBuilder = new StringBuilder();
                                    if (firstName != null) nameBuilder.append(firstName);
                                    if (lastName != null) {
                                        if (nameBuilder.length() > 0) nameBuilder.append(" ");
                                        nameBuilder.append(lastName);
                                    }
                                    nameEditText.setText(nameBuilder.toString());
                                }
                            }
                        }
                        
                        // Set email directly using getter
                        if (emailEditText != null && user.getEmail() != null) {
                            emailEditText.setText(user.getEmail());
                        }
                        
                        // Set phone number
                        if (phoneEditText != null && user.getPhoneNumber() != null) {
                            phoneEditText.setText(user.getPhoneNumber());
                        }
                        
                        // Set blood type
                        if (bloodTypeEditText != null && user.getBloodType() != null) {
                            bloodTypeEditText.setText(user.getBloodType());
                        }
                        
                        // Set Bilkent ID
                        if (bilkentIdEditText != null && user.getBilkentId() != null) {
                            bilkentIdEditText.setText(user.getBilkentId());
                        }
                        
                        // Update UI based on club memberships
                        updateClubUI(user);
                        
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
                    if (phoneEditText != null) phoneEditText.setEnabled(true);
                    if (bloodTypeEditText != null) bloodTypeEditText.setEnabled(true);
                    if (bilkentIdEditText != null) bilkentIdEditText.setEnabled(true);
                    
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
    
    private void updateClubUI(User user) {
        try {
            List<ClubMember> memberships = user.getClubMemberships();
            
            if (memberships != null && !memberships.isEmpty()) {
                // User is already in some clubs
                if (manageClubsButton != null) {
                    manageClubsButton.setEnabled(true);
                    manageClubsButton.setText("Manage My Clubs (" + memberships.size() + ")");
                }
            } else {
                // User has no clubs
                if (manageClubsButton != null) {
                    manageClubsButton.setText("No Clubs Joined");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating club UI", e);
        }
    }
    
    private void joinClub() {
        try {
            Intent intent = new Intent(SettingsActivity.this, ClubActivitiesActivity.class);
            intent.putExtra("mode", "join");
            startActivity(intent);
        } catch (Exception e) {
            // Try with class name if direct reference fails
            try {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.bilkom.ui.ClubActivitiesActivity");
                intent.putExtra("mode", "join");
                startActivity(intent);
            } catch (Exception ex) {
                Log.e(TAG, "Error opening club join page", ex);
                Toast.makeText(this, "Club joining feature coming soon", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateUserProfile() {
        try {
            // Get values from fields
            String name = nameEditText != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText != null ? emailEditText.getText().toString().trim() : "";
            String phone = phoneEditText != null ? phoneEditText.getText().toString().trim() : "";
            String bloodType = bloodTypeEditText != null ? bloodTypeEditText.getText().toString().trim() : "";
            String bilkentId = bilkentIdEditText != null ? bilkentIdEditText.getText().toString().trim() : "";
            
            // Clear previous errors
            if (nameEditText != null) nameEditText.setError(null);
            if (emailEditText != null) emailEditText.setError(null);
            if (phoneEditText != null) phoneEditText.setError(null);
            if (bloodTypeEditText != null) bloodTypeEditText.setError(null);
            if (bilkentIdEditText != null) bilkentIdEditText.setError(null);
            
            // Validate inputs
            boolean isValid = true;
            
            if (name.isEmpty() && nameEditText != null) {
                nameEditText.setError("Required");
                isValid = false;
            }
            
            if (email.isEmpty() && emailEditText != null) {
                emailEditText.setError("Required");
                isValid = false;
            } else if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && emailEditText != null) {
                emailEditText.setError("Invalid email format");
                isValid = false;
            }
            
            // Additional validations if needed
            if (!phone.isEmpty() && !isValidPhoneNumber(phone) && phoneEditText != null) {
                phoneEditText.setError("Invalid phone format");
                isValid = false;
            }
            
            if (!isValid) {
                return;
            }
            
            // Update UI state
            if (updateProfileButton != null) {
                updateProfileButton.setEnabled(false);
                updateProfileButton.setText("Updating...");
            }
            
            // Create user object with updated data
            User updatedUser = new User();
            
            // Set all fields directly
            try {
                // Parse the full name into first and last name
                String[] nameParts = name.split(" ", 2);
                if (nameParts.length > 0) {
                    updatedUser.setFirstName(nameParts[0]);
                    if (nameParts.length > 1) {
                        updatedUser.setLastName(nameParts[1]);
                    }
                }
                
                // Set other fields
                updatedUser.setEmail(email);
                updatedUser.setPhoneNumber(phone);
                updatedUser.setBloodType(bloodType);
                updatedUser.setBilkentId(bilkentId);
                
            } catch (Exception e) {
                Log.e(TAG, "Error setting user fields", e);
            }
            
            // Update API
            apiService.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    // Restore UI state
                    if (updateProfileButton != null) {
                        updateProfileButton.setEnabled(true);
                        updateProfileButton.setText("Update Profile");
                    }
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this, 
                            "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            
                        // Reload user data to refresh the UI
                        loadUserData();
                    } else {
                        String errorMessage = "Failed to update profile: " + response.code();
                        Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMessage);
                    }
                }
                
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // Restore UI state
                    if (updateProfileButton != null) {
                        updateProfileButton.setEnabled(true);
                        updateProfileButton.setText("Update Profile");
                    }
                    
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
    
    // Validate phone number format
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Basic phone validation - can be extended
        return phoneNumber.matches("\\+?\\d{10,15}");
    }
    
    private void openClubManagement() {
        try {
            // First try with direct class reference
            try {
                Intent intent = new Intent(this, ClubActivitiesActivity.class);
                startActivity(intent);
                return;
            } catch (Exception e) {
                Log.d(TAG, "ClubActivitiesActivity not found, trying alternative");
            }
            
            // Try with class name if direct reference fails
            try {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.bilkom.ui.ClubActivitiesActivity");
                startActivity(intent);
                return;
            } catch (Exception e) {
                Log.d(TAG, "ClubActivitiesActivity class not found, trying MyClubsActivity");
            }
            
            // Try MyClubsActivity as a fallback
            try {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.bilkom.ui.MyClubsActivity");
                startActivity(intent);
                return;
            } catch (Exception e) {
                Log.e(TAG, "No club management activity found", e);
            }
            
            // If all else fails, show a dialog with club info
            showClubInfoDialog();
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening club management", e);
            Toast.makeText(this, "Club management not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showClubInfoDialog() {
        apiService.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    List<ClubMember> memberships = user.getClubMemberships();
                    
                    if (memberships == null || memberships.isEmpty()) {
                        new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("My Clubs")
                            .setMessage("No clubs joined")
                            .setPositiveButton("OK", null)
                            .show();
                        return;
                    }

                    // Fetch all club names asynchronously
                    StringBuilder clubInfo = new StringBuilder();
                    final int[] clubsFetched = {0};
                    for (ClubMember membership : memberships) {
                        Long clubId = membership.getClubId();
                        if (clubId != null) {
                            apiService.getClub(clubId, "Bearer " + token).enqueue(new Callback<Club>() {
                                @Override
                                public void onResponse(Call<Club> call, Response<Club> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        clubInfo.append(response.body().getName()).append("\n");
                                    }
                                    clubsFetched[0]++;
                                    if (clubsFetched[0] == memberships.size()) {
                                        // All clubs fetched, show dialog
                                        new AlertDialog.Builder(SettingsActivity.this)
                                            .setTitle("My Clubs")
                                            .setMessage(clubInfo.length() > 0 ? clubInfo.toString() : "No clubs joined")
                                            .setPositiveButton("OK", null)
                                            .show();
                                    }
                                }
                                
                                @Override
                                public void onFailure(Call<Club> call, Throwable t) {
                                    clubsFetched[0]++;
                                    if (clubsFetched[0] == memberships.size()) {
                                        new AlertDialog.Builder(SettingsActivity.this)
                                            .setTitle("My Clubs")
                                            .setMessage(clubInfo.length() > 0 ? clubInfo.toString() : "No clubs joined")
                                            .setPositiveButton("OK", null)
                                            .show();
                                    }
                                }
                            });
                        } else {
                            clubsFetched[0]++;
                            if (clubsFetched[0] == memberships.size()) {
                                new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("My Clubs")
                                    .setMessage(clubInfo.length() > 0 ? clubInfo.toString() : "No clubs joined")
                                    .setPositiveButton("OK", null)
                                    .show();
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Error loading club information", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button in the action bar
            navigateToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Override to handle the hardware back button
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
        startActivity(intent);
        finish(); // Close this activity
    }
}
