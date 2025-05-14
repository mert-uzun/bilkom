// this is the main activity class for the main page
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.bilkom.R;
import com.bilkom.utils.SecureStorage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SecureStorage secureStorage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "Main Activity loaded", Toast.LENGTH_SHORT).show();
            
            secureStorage = new SecureStorage(this);
            
            // Set up menu button
            Button menuButton = findViewById(R.id.menuButton);
            if (menuButton != null) {
                Log.d(TAG, "Menu button found");
                menuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Menu button clicked");
                        Toast.makeText(MainActivity.this, "Menu button clicked", Toast.LENGTH_SHORT).show();
                        showMenu(v);
                    }
                });
            } else {
                Log.e(TAG, "Menu button not found in layout");
                Toast.makeText(this, "Menu button not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void showMenu(View v) {
        try {
            Log.d(TAG, "Attempting to show menu");
            
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
            Log.d(TAG, "Menu inflated successfully");
            
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    Log.d(TAG, "Menu item clicked: " + item.getTitle());
                    
                    if (itemId == R.id.menu_home) {
                        // Already on home page
                        Toast.makeText(MainActivity.this, "You are already on Home page", Toast.LENGTH_SHORT).show();
                        return true;
                    } 
                    else if (itemId == R.id.menu_profile) {
                        // Navigate to profile
                        try {
                            Intent intent = new Intent(MainActivity.this, Class.forName("com.bilkom.ui.ProfileActivity"));
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "ProfileActivity not found", e);
                            Toast.makeText(MainActivity.this, "Profile page is not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } 
                    else if (itemId == R.id.menu_settings) {
                        // Navigate to settings
                        try {
                            Intent intent = new Intent(MainActivity.this, Class.forName("com.bilkom.ui.SettingsActivity"));
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "SettingsActivity not found", e);
                            Toast.makeText(MainActivity.this, "Settings page is not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } 
                    else if (itemId == R.id.menu_logout) {
                        // Logout
                        secureStorage.clearAll();
                        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to login
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    
                    return false;
                }
            });
            
            popup.show();
            Log.d(TAG, "Menu should be displayed now");
            Toast.makeText(this, "Menu displayed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing menu", e);
            Toast.makeText(this, "Error showing menu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
} 