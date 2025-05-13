// this is the base activity class for the home, profile and settings activities
// it is used to store the base activity content
// it extends the AppCompatActivity class to show the menu 
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;
import androidx.drawerlayout.widget.DrawerLayout.GravityCompat;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.navigation.NavigationView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;


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
    protected NavigationView navigationView;
    protected Toolbar toolbar;
    protected FrameLayout contentFrame;

    private static final int NAV_HOME = 0x7f0900a1;      
    private static final int NAV_EVENTS = 0x7f0900a2;   
    private static final int NAV_CLUBS = 0x7f0900a3;     
    private static final int NAV_PROFILE = 0x7f0900a4;  
    private static final int NAV_SETTINGS = 0x7f0900a5;  
    private static final int NAV_LOGOUT = 0x7f0900a6;    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getBaseLayoutId());
        
        try {
            toolbar = findViewById(getToolbarId());
            setSupportActionBar(toolbar);

            drawerLayout = findViewById(getDrawerLayoutId());
            navigationView = findViewById(getNavViewId());
            contentFrame = findViewById(getContentFrameId());

            if (drawerLayout != null && navigationView != null && toolbar != null) {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawerLayout, toolbar, 
                        getNavigationDrawerOpenId(), getNavigationDrawerCloseId());
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();

                navigationView.setNavigationItemSelectedListener(this);
                setupUserInfo();
            } else {
                Log.w(TAG, "Some navigation components are missing");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation", e);
        }

        setupCornerMenu();
    }
    
    private void setupUserInfo() {
        try {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                TextView navUsername = headerView.findViewById(getNavHeaderUsernameId());
                if (navUsername != null) {
                    SecureStorage secureStorage = new SecureStorage(this);
                    String token = secureStorage.getAuthToken();
                    if (token != null && !token.isEmpty()) {
                        navUsername.setText("User #" + secureStorage.getUserId());
                    } else {
                        navUsername.setText("Guest");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up user info", e);
        }
    }

    protected int getBaseLayoutId() {
        return 0x7f0c001c; 
    }
    
    protected int getToolbarId() {
        return 0x7f09017c; 
    }
    
    protected int getDrawerLayoutId() {
        return 0x7f090071; 
    }
    
    protected int getNavViewId() {
        return 0x7f0900e4; 
    }
    
    protected int getContentFrameId() {
        return 0x7f09005a;
    }
    
    protected int getNavHeaderUsernameId() {
        return 0x7f0900e3;
    }
    
    protected int getNavigationDrawerOpenId() {
        return 0x7f1000a1;
    }
    
    protected int getNavigationDrawerCloseId() {
        return 0x7f1000a0; 
    }

    protected void setupNavigationDrawer() {
        if (navigationView != null) {
            navigationView.setCheckedItem(getNavigationMenuItemId());
        }
    }

    protected int getNavigationMenuItemId() {
        Log.d(TAG, "Home ID: " + R.id.nav_home);
        return R.id.nav_home;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Menu item clicked with ID: " + id);
        
        try {
            if (id == NAV_HOME && !(this instanceof HomeActivity)) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == NAV_EVENTS) {
                try {
                    Class<?> eventsClass = Class.forName("com.bilkom.ui.EventsActivity");
                    startActivity(new Intent(this, eventsClass));
                } catch (ClassNotFoundException e) {
                    Toast.makeText(this, "Events feature coming soon", Toast.LENGTH_SHORT).show();
                }
            } else if (id == NAV_CLUBS) {
                handleNavigationToClubs();
            } else if (id == NAV_PROFILE) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == NAV_SETTINGS && !(this instanceof SettingsActivity)) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == NAV_LOGOUT) {
                performLogout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation selection", e);
        }
        return true;
    }
    
    private void handleNavigationToClubs() {
        try {
            Class<?> clubsActivityClass = Class.forName("com.bilkom.ui.ClubsActivity");
            startActivity(new Intent(this, clubsActivityClass));
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Clubs feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void performLogout() {
        try {
            SecureStorage secureStorage = new SecureStorage(this);
            secureStorage.clearAll();
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    protected void setupCornerMenu() {
        try {

            int menuButtonId = getResources().getIdentifier("menuButton", "id", getPackageName());
            if (menuButtonId != 0) {
                menuButton = findViewById(menuButtonId);
                if (menuButton != null) {
                    menuButton.setOnClickListener(v -> showMenu(v));
                } else {
                    Log.e(TAG, "Menu button not found in layout");
                }
            } else {
                Log.e(TAG, "Menu button ID not found in resources");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up corner menu", e);
        }
    }
    
    private void showMenu(View v) {
        try {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add(0, 1, 0, "Home");
            popup.getMenu().add(0, 2, 0, "Profile");
            popup.getMenu().add(0, 3, 0, "Settings");
            popup.getMenu().add(0, 4, 0, "Logout");
            
            popup.setOnMenuItemClickListener(item -> {
                try {
                    switch (item.getItemId()) {
                        case 1: 
                            if (!(this instanceof HomeActivity)) {
                                startActivity(new Intent(this, HomeActivity.class));
                            }
                            return true;
                            
                        case 2: 
                            if (!(this instanceof ProfileActivity)) {
                                try {
                                    Intent profileIntent = new Intent(this, ProfileActivity.class);
                                    startActivity(profileIntent);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error starting ProfileActivity directly", e);
                                    
                                    try {
                                        Class<?> profileClass = Class.forName("com.bilkom.ui.ProfileActivity");
                                        Intent intent = new Intent(this, profileClass);
                                        startActivity(intent);
                                    } catch (ClassNotFoundException cnfe) {
                                        Log.e(TAG, "ProfileActivity class not found", cnfe);
                                        Toast.makeText(this, "Profile page not available", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            return true;
                            
                        case 3: // Settings
                            if (!(this instanceof SettingsActivity)) {
                                startActivity(new Intent(this, SettingsActivity.class));
                            }
                            return true;
                            
                        case 4: // Logout
                            logout();
                            return true;
                            
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling menu selection", e);
                    Toast.makeText(BaseActivity.this, "Error navigating to selected page", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            
            popup.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing menu", e);
            Toast.makeText(this, "Error showing menu", Toast.LENGTH_SHORT).show();
        }
    }
    
    protected void logout() {
        try {
            SecureStorage secureStorage = new SecureStorage(this);
            secureStorage.clearAll();
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }
} 