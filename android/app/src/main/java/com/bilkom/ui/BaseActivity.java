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

    // Define navigation item IDs as constants
    private static final int NAV_HOME = 0x7f0900a1;      
    private static final int NAV_EVENTS = 0x7f0900a2;   
    private static final int NAV_CLUBS = 0x7f0900a3;     
    private static final int NAV_PROFILE = 0x7f0900a4;  
    private static final int NAV_SETTINGS = 0x7f0900a5;  
    private static final int NAV_LOGOUT = 0x7f0900a6;    
    
    // These can be accessed as R.id.nav_settings and R.id.nav_clubs
    public static final int nav_settings = NAV_SETTINGS;
    public static final int nav_clubs = NAV_CLUBS;

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

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);

                navigationView.setNavigationItemSelectedListener(this);
                setupUserInfo();
            } else {
                Log.w(TAG, "Some navigation components are missing");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation", e);
        }

        setupCornerMenu();

        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();
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
        Log.d(TAG, "Menu item clicked with ID: " + id + ", title: " + item.getTitle());
        
        try {
            if (id == R.id.nav_home && !(this instanceof HomeActivity)) {
                Log.d(TAG, "Navigating to HomeActivity");
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_events) {
                Log.d(TAG, "Navigating to EventActivity");
                startActivity(new Intent(this, EventActivity.class));
            } else if (id == R.id.nav_profile && !(this instanceof ProfileActivity)) {
                Log.d(TAG, "Navigating to ProfileActivity");
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == nav_settings && !(this instanceof SettingsActivity)) {
                Log.d(TAG, "Navigating to SettingsActivity");
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == nav_clubs) {
                Log.d(TAG, "Navigating to ClubActivitiesActivity");
                handleNavigationToClubs();
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
    
    private void handleNavigationToClubs() {
        try {
            startActivity(new Intent(this, ClubActivitiesActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Clubs feature coming soon", Toast.LENGTH_SHORT).show();
        }
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
        Intent intent = new Intent(this, LoginActivity.class);
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
                    startActivity(new Intent(this, HomeActivity.class));
                }
            });
            
            View profileItem = popupView.findViewById(R.id.menu_profile);
            profileItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                if (!(this instanceof ProfileActivity)) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
            
            View settingsItem = popupView.findViewById(R.id.menu_settings);
            settingsItem.setOnClickListener(v -> {
                popupWindow.dismiss();
                if (!(this instanceof SettingsActivity)) {
                    Intent intent = new Intent(this, SettingsActivity.class);
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