// this is the main activity class for the main page
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Setup button click listeners first
        setupButtonListeners();
        
        // Redirect to HomeActivity after a short delay to allow button clicks
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so user can't go back to it
        }, 3000); // 3-second delay to allow users to interact with buttons
    }
    
    private void setupButtonListeners() {
        // Activity Selection button
        Button activitySelectionButton = findViewById(R.id.activitySelectionButton);
        if (activitySelectionButton != null) {
            activitySelectionButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(intent);
                    finish(); // Close MainActivity
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to activity selection", e);
                    Toast.makeText(this, "Cannot open activity selection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Club Activities button
        Button clubActivitiesButton = findViewById(R.id.clubActivitiesButton);
        if (clubActivitiesButton != null) {
            clubActivitiesButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, ClubActivitiesActivity.class);
                    startActivity(intent);
                    finish(); // Close MainActivity
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to club activities", e);
                    Toast.makeText(this, "Cannot open club activities", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Emergency Alerts button
        Button emergencyAlertsButton = findViewById(R.id.emergencyAlertsButton);
        if (emergencyAlertsButton != null) {
            emergencyAlertsButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(intent);
                    finish(); // Close MainActivity
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to emergency alerts", e);
                    Toast.makeText(this, "Cannot open emergency alerts", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
} 