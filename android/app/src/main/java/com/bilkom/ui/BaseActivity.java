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
import com.bilkom.utils.SecureStorage;
import com.google.android.material.navigation.NavigationView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.bilkom.R;
import com.bilkom.model.User;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.app.AlertDialog;


/**
 * BaseActivity class for managing common functionality across activities.
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "BaseActivity";
    protected DrawerLayout drawerLayout;
    protected View navView;
    protected ImageButton menuButton;
    protected NavigationView navigationView;
    protected Toolbar toolbar;
    protected FrameLayout contentFrame;
    protected SecureStorage secureStorage;
    protected ApiService apiService;

    // Define navigation item IDs as constants - use only those that exist in the menu
    private static final int NAV_HOME = R.id.nav_home;      
    private static final int NAV_EVENTS = R.id.nav_events;   
    private static final int NAV_PROFILE = R.id.nav_profile;  
    private static final int NAV_LOGOUT = R.id.nav_logout;    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // First set the content view
        setContentView(getBaseLayoutId());
        
        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();
        
        try {
            // Find and set up the toolbar if it exists
            toolbar = findViewById(getToolbarId());
            
            // Find the drawer layout and navigation view
            drawerLayout = findViewById(getDrawerLayoutId());
            navigationView = findViewById(getNavViewId());
            contentFrame = findViewById(getContentFrameId());
            
            // Set up the toolbar if it exists
            if (toolbar != null) {
                try {
                    setSupportActionBar(toolbar);
                } catch (IllegalStateException e) {
                    Log.w(TAG, "Activity already has an action bar, not setting Toolbar: " + e.getMessage());
                }
            }

            // Set up the navigation drawer if both drawer layout and navigation view exist
            if (drawerLayout != null && navigationView != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }
                
                if (toolbar != null) {
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawerLayout, toolbar,
                            getNavigationDrawerOpenId(), getNavigationDrawerCloseId());
                    drawerLayout.addDrawerListener(toggle);
                    toggle.syncState();
                }

                navigationView.setNavigationItemSelectedListener(this);
                setupUserInfo();
            }
            
            // Set up the corner menu button
            setupCornerMenu();
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation", e);
        }
    }
    
    private void setupUserInfo() {
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView nameTextView = headerView.findViewById(R.id.nav_header_username);

            String token = secureStorage.getAuthToken();
            if (token != null && !token.isEmpty()) {
                apiService.getUser(secureStorage.getUserId()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            nameTextView.setText(user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Error setting up user info", t);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up user info", e);
        }
    }

    protected int getBaseLayoutId() {
        return R.layout.activity_base; 
    }
    
    protected int getToolbarId() {
        return R.id.toolbar; 
    }
    
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout; 
    }
    
    protected int getNavViewId() {
        return R.id.nav_view; 
    }
    
    protected int getContentFrameId() {
        return R.id.contentFrame;
    }
    
    protected int getNavHeaderUsernameId() {
        return R.id.nav_header_username;
    }
    
    protected int getNavigationDrawerOpenId() {
        return R.string.navigation_drawer_open;
    }
    
    protected int getNavigationDrawerCloseId() {
        return R.string.navigation_drawer_close; 
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
        Log.d(TAG, "Menu item clicked with ID: " + id + ", title: " + item.getTitle());
        
        try {
            Intent intent = new Intent();
            
            if (id == R.id.nav_home && !(this instanceof HomeActivity)) {
                Log.d(TAG, "Navigating to HomeActivity");
                intent.setClassName(getPackageName(), "com.bilkom.ui.HomeActivity");
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                Log.d(TAG, "Navigating to EventActivity");
                intent.setClassName(getPackageName(), "com.bilkom.ui.EventActivity");
                startActivity(intent);
            } else if (id == R.id.nav_profile && !(this instanceof ProfileActivity)) {
                Log.d(TAG, "Navigating to ProfileActivity");
                intent.setClassName(getPackageName(), "com.bilkom.ui.ProfileActivity");
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                Log.d(TAG, "Handling logout");
                handleLogout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation selection", e);
        }
        return true;
    }
    
    // Method to navigate to club activities
    protected void navigateToClubActivities() {
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), "com.bilkom.ui.ClubActivitiesActivity");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Clubs feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Method to navigate to settings
    protected void navigateToSettings() {
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), "com.bilkom.ui.SettingsActivity");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Settings feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleNavigationToClubs() {
        navigateToClubActivities();
    }
    
    private void handleLogout() {
        try {
            String token = secureStorage.getAuthToken();
            if (token != null && !token.isEmpty()) {
                apiService.logout("Bearer " + token).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "Logout API call successful: " + response.code());
                        performLocalLogout();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Logout API call failed", t);
                        performLocalLogout();
                    }
                });
            } else {
                performLocalLogout();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    private void performLocalLogout() {
        secureStorage.clearAll();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.bilkom.ui.LoginActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerLayout != null && item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    protected void setupCornerMenu() {
        try {
            // The menu button might be in the toolbar
            ImageButton menuButton = findViewById(R.id.menuButton);
            if (menuButton == null) {
                // Try to find it in the toolbar
                if (toolbar != null) {
                    menuButton = toolbar.findViewById(R.id.menuButton);
                }
            }
            
            if (menuButton == null) {
                Log.e(TAG, "Menu button not found in layout or toolbar");
                return;
            }
            
            Log.d(TAG, "Menu button found, setting onClick listener");
            menuButton.setOnClickListener(v -> {
                showCustomMenu(v);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up corner menu", e);
        }
    }
    
    private void showCustomMenu(View anchorView) {
        try {
            // Inflate the popup menu layout
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(R.layout.popup_menu, null);
            
            PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            );
            
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setElevation(24);
            
            View homeItem = popupView.findViewById(R.id.menu_home);
            homeItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                if (!(this instanceof HomeActivity)) {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), "com.bilkom.ui.HomeActivity");
                    startActivity(intent);
                }
            });
            
            View profileItem = popupView.findViewById(R.id.menu_profile);
            profileItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                if (!(this instanceof ProfileActivity)) {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), "com.bilkom.ui.ProfileActivity");
                    startActivity(intent);
                }
            });
            
            View settingsItem = popupView.findViewById(R.id.menu_settings);
            settingsItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                if (!(this instanceof SettingsActivity)) {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), "com.bilkom.ui.SettingsActivity");
                    startActivity(intent);
                }
            });
            
            View logoutItem = popupView.findViewById(R.id.menu_logout);
            logoutItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                new AlertDialog.Builder(this)
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        handleLogout();
                    })
                    .setNegativeButton("No", null)
                    .show();
            });
            
            popupWindow.showAsDropDown(anchorView, 0, 0);
        } catch (Exception e) {
            Log.e(TAG, "Error showing menu popup", e);
            Toast.makeText(this, "Error showing menu", Toast.LENGTH_SHORT).show();
        }
    }
    
    protected void logout() {
        handleLogout(); 
    }
} 