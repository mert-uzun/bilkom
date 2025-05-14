package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bilkom.ui.BaseActivity;
import com.bilkom.R;
import com.bilkom.model.ClubMember;
import com.bilkom.model.User;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ProfileActivity extends BaseActivity {
    private TextView emailText, firstNameText, lastNameText, bilkentIdText, phoneText, bloodTypeText;
    private SecureStorage secureStorage;
    private LinearLayout clubsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        
        // Inflate the profile content into the content frame
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.contentFrame));

        secureStorage = new SecureStorage(this);
        emailText = findViewById(R.id.profileEmail);
        firstNameText = findViewById(R.id.profileFirstName);
        lastNameText = findViewById(R.id.profileLastName);
        bilkentIdText = findViewById(R.id.profileBilkentId);
        phoneText = findViewById(R.id.profilePhone);
        bloodTypeText = findViewById(R.id.profileBloodType);
        clubsContainer = findViewById(R.id.clubsContainer);
        
        findViewById(R.id.editProfileButton).setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, Class.forName("com.bilkom.ui.SettingsActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.e("ProfileActivity", "Error navigating to SettingsActivity: " + e.getMessage());
                Toast.makeText(ProfileActivity.this, "Cannot open settings page", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserProfile();
    }

    private void loadUserProfile() {
        Long userId = secureStorage.getUserId();
        String token = secureStorage.getAuthToken();
        if (userId == null || token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitClient.getInstance().getApiService().getUser(userId)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        emailText.setText(user.getEmail());
                        firstNameText.setText(user.getFirstName());
                        lastNameText.setText(user.getLastName());
                        bilkentIdText.setText(user.getBilkentId());
                        phoneText.setText(user.getPhoneNumber());
                        bloodTypeText.setText(user.getBloodType());
                        displayClubs(user.getClubMemberships());
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void displayClubs(List<ClubMember> clubMemberships) {
        clubsContainer.removeAllViews();
        if (clubMemberships != null && !clubMemberships.isEmpty()) {
            for (ClubMember member : clubMemberships) {
                TextView clubText = new TextView(this);
                // Display user's club info - use name from User object if available
                String clubInfo = member.getUser() != null ? 
                    member.getUser().getFullName() : 
                    "Club ID: " + member.getClubId();
                clubText.setText(clubInfo);
                clubText.setPadding(0, 0, 0, 16);
                clubText.setClickable(true);
                clubText.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                
                // Make club names clickable to view club details
                final Long clubId = member.getClubId();
                clubText.setOnClickListener(v -> {
                    try {
                        // Navigate to club activities
                        Intent intent = new Intent(this, Class.forName("com.bilkom.ui.ClubActivitiesActivity"));
                        intent.putExtra("clubId", clubId);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Club details not available", Toast.LENGTH_SHORT).show();
                    }
                });
                
                clubsContainer.addView(clubText);
            }
        } else {
            TextView noClubsText = new TextView(this);
            noClubsText.setText("No club memberships");
            clubsContainer.addView(noClubsText);
        }
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.activity_base;
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