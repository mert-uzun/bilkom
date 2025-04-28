// this is the base activity class for the home, profile and settings activities
// it is used to store the base activity content
// it extends the AppCompatActivity class to show the menu 
package com.bilkom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bilkom.ui.HomeActivity;
import com.bilkom.ui.LoginActivity;
import com.bilkom.ui.ProfileActivity;
import com.bilkom.ui.SettingsActivity;

public class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected View navView;
    protected ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        menuButton = findViewById(R.id.menuButton);

        // Setup menu button
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Setup navigation items
        setupNavigationItems();
    }

    private void setupNavigationItems() {
        // Profile
        findViewById(R.id.profileMenuItem).setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.LEFT);
            if (!(this instanceof ProfileActivity)) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });

        // Home
        findViewById(R.id.homeMenuItem).setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.LEFT);
            if (!(this instanceof HomeActivity)) {
                startActivity(new Intent(this, HomeActivity.class));
                finishAffinity();
            }
        });

        // Settings
        findViewById(R.id.settingsMenuItem).setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.LEFT);
            if (!(this instanceof SettingsActivity)) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
        });
    }
} 