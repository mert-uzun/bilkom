package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bilkom.R;
import com.bilkom.model.Club;
import com.bilkom.model.ClubRequest;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateClubActivity extends BaseActivity {
    private static final String TAG = "CreateClubActivity";
    
    private TextInputEditText clubNameEditText;
    private TextInputEditText clubDescriptionEditText;
    private TextInputEditText executivePositionEditText;
    private TextInputEditText verificationDocUrlEditText;
    private TextInputEditText additionalInfoEditText;
    private Button createClubButton;
    private TextView statusMessageTextView;
    
    private ApiService apiService;
    private SecureStorage secureStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);
        setupNavigationDrawer();
        
        // Initialize views
        initializeViews();
        
        // Initialize API service and secure storage
        apiService = RetrofitClient.getInstance().getApiService();
        secureStorage = new SecureStorage(this);
        
        // Set click listener for create button
        createClubButton.setOnClickListener(v -> validateAndSubmitClub());
    }
    
    private void initializeViews() {
        clubNameEditText = findViewById(R.id.clubNameEditText);
        clubDescriptionEditText = findViewById(R.id.clubDescriptionEditText);
        executivePositionEditText = findViewById(R.id.executivePositionEditText);
        verificationDocUrlEditText = findViewById(R.id.verificationDocUrlEditText);
        additionalInfoEditText = findViewById(R.id.additionalInfoEditText);
        createClubButton = findViewById(R.id.createClubButton);
        statusMessageTextView = findViewById(R.id.statusMessageTextView);
    }
    
    private void validateAndSubmitClub() {
        // Clear any previous errors
        clubNameEditText.setError(null);
        clubDescriptionEditText.setError(null);
        executivePositionEditText.setError(null);
        verificationDocUrlEditText.setError(null);
        
        // Get input values
        String clubName = clubNameEditText.getText().toString().trim();
        String clubDescription = clubDescriptionEditText.getText().toString().trim();
        String executivePosition = executivePositionEditText.getText().toString().trim();
        String verificationDocUrl = verificationDocUrlEditText.getText().toString().trim();
        String additionalInfo = additionalInfoEditText.getText().toString().trim();
        
        // Validate required fields
        boolean isValid = true;
        
        if (clubName.isEmpty()) {
            clubNameEditText.setError("Club name is required");
            isValid = false;
        } else if (clubName.length() < 3) {
            clubNameEditText.setError("Club name must be at least 3 characters");
            isValid = false;
        }
        
        if (clubDescription.isEmpty()) {
            clubDescriptionEditText.setError("Club description is required");
            isValid = false;
        } else if (clubDescription.length() < 10) {
            clubDescriptionEditText.setError("Description must be at least 10 characters");
            isValid = false;
        }
        
        if (executivePosition.isEmpty()) {
            executivePositionEditText.setError("Your position in the club is required");
            isValid = false;
        }
        
        if (verificationDocUrl.isEmpty()) {
            verificationDocUrlEditText.setError("Verification document URL is required");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Create the club request
        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubName(clubName);
        clubRequest.setClubDescription(clubDescription);
        clubRequest.setExecutiveUserId(secureStorage.getUserId());
        clubRequest.setExecutivePosition(executivePosition);
        clubRequest.setVerificationDocumentUrl(verificationDocUrl);
        
        if (!additionalInfo.isEmpty()) {
            clubRequest.setAdditionalInfo(additionalInfo);
        }
        
        // Show loading status
        statusMessageTextView.setVisibility(View.VISIBLE);
        statusMessageTextView.setText("Submitting club registration...");
        createClubButton.setEnabled(false);
        
        // Submit the club registration request
        String token = "Bearer " + secureStorage.getAuthToken();
        apiService.registerClub(clubRequest).enqueue(new Callback<Club>() {
            @Override
            public void onResponse(Call<Club> call, Response<Club> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Show success message
                    statusMessageTextView.setText("Club registration submitted successfully! Waiting for approval.");
                    Toast.makeText(CreateClubActivity.this, 
                        "Club registration submitted! Waiting for approval.", 
                        Toast.LENGTH_LONG).show();
                    
                    // Disable the form fields
                    setFieldsEnabled(false);
                    
                    // Add delay before returning to the previous screen
                    statusMessageTextView.postDelayed(() -> {
                        finish();
                    }, 3000);
                } else {
                    // Show error message
                    Log.e(TAG, "Error registering club: " + response.code() + " " + response.message());
                    statusMessageTextView.setText("Failed to register club. Please try again.");
                    createClubButton.setEnabled(true);
                    
                    String errorMsg = "Registration failed. ";
                    if (response.code() == 400) {
                        errorMsg += "Please check your input.";
                    } else if (response.code() == 401) {
                        errorMsg += "Please log in again.";
                    } else {
                        errorMsg += "Server error.";
                    }
                    
                    Toast.makeText(CreateClubActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Club> call, Throwable t) {
                Log.e(TAG, "Network error registering club", t);
                statusMessageTextView.setText("Network error. Please check your connection.");
                createClubButton.setEnabled(true);
                Toast.makeText(CreateClubActivity.this, 
                    "Network error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setFieldsEnabled(boolean enabled) {
        clubNameEditText.setEnabled(enabled);
        clubDescriptionEditText.setEnabled(enabled);
        executivePositionEditText.setEnabled(enabled);
        verificationDocUrlEditText.setEnabled(enabled);
        additionalInfoEditText.setEnabled(enabled);
        createClubButton.setEnabled(enabled);
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