package com.bilkom.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bilkom.BaseActivity;
import com.bilkom.R;
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

        loadUserProfile();
    }

    private void loadUserProfile() {
        Long userId = secureStorage.getUserId();
        String token = secureStorage.getAuthToken();
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

    private void displayClubs(List<String> clubMemberships) {
        clubsContainer.removeAllViews();
        if (clubMemberships != null && !clubMemberships.isEmpty()) {
            for (String club : clubMemberships) {
                TextView clubText = new TextView(this);
                clubText.setText(club);
                clubText.setPadding(0, 0, 0, 16);
                clubsContainer.addView(clubText);
            }
        } else {
            TextView noClubsText = new TextView(this);
            noClubsText.setText("No club memberships");
            clubsContainer.addView(noClubsText);
        }
    }
} 