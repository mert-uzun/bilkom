// this is the base activity class for the home, profile and settings activities
// it is used to store the base activity content
// it extends the AppCompatActivity class to show the menu 
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.navigation.NavigationView;


/**
 * BaseActivity class for managing common functionality across activities.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected View navView;
    protected ImageButton menuButton;
    protected androidx.appcompat.widget.Toolbar toolbar;
    protected NavigationView navigationView;
    protected View contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Setup toolbar with null checks
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        contentFrame = findViewById(R.id.contentFrame);

        // Only setup drawer if all components exist
        if (drawerLayout != null && navigationView != null && toolbar != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);
            
            // Set user info with null checks
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                TextView navUsername = headerView.findViewById(R.id.nav_header_username);
                if (navUsername != null) {
                    SecureStorage secureStorage = new SecureStorage(this);
                    try {
                        if (!secureStorage.getAuthToken().isEmpty()) {
                            navUsername.setText("User #" + secureStorage.getUserId());
                        } else {
                            navUsername.setText("Guest");
                        }
                    } catch (Exception e) {
                        Log.e("BaseActivity", "Error setting user info", e);
                        navUsername.setText("User");
                    }
                }
            }
        }
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