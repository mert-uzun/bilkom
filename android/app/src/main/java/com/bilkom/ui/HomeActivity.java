/**
 * @author Sıla Bozkurt
 * @version 1.0
 * 
 * This is the home activity class for the home page.
 * It is used to store the home page content.
 * It extends the BaseActivity class to show the menu.
 */
package com.bilkom.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.ComponentName;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bilkom.ui.BaseActivity;
import com.bilkom.R;
import com.bilkom.model.EmergencyAlert;
import com.bilkom.model.News;
import com.bilkom.model.WeatherForecast;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.WeatherIconUtils;
import com.bilkom.adapter.EventAdapter;
import com.bilkom.model.Event;
import com.bilkom.utils.SecureStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    
    private TextView weatherTemp;
    private TextView weatherDesc;
    private ImageView weatherIcon;
    private LinearLayout newsContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use a simpler, more direct inflation method
        setContentView(R.layout.activity_home);
        
        setupNavigationDrawer();
        
        // Initialize views with better error handling
        try {
            weatherTemp = findViewById(R.id.weatherTemp);
            weatherDesc = findViewById(R.id.weatherDesc);
            weatherIcon = findViewById(R.id.weatherIcon);
            newsContainer = findViewById(R.id.newsContainer);
            
            if (weatherTemp == null || weatherDesc == null || weatherIcon == null || newsContainer == null) {
                Log.e(TAG, "Failed to initialize weather or news views");
            } else {
                // Load weather and news data
                loadWeatherData();
                loadNewsData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
        
        // Setup clickable areas if needed - move this after view initialization
        setupClickListeners();
        
        // Handle navigation requests from MainActivity
        handleNavigationRequests();
    }
    
    private void handleNavigationRequests() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.getBooleanExtra("openProfileAfterLoad", false)) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
                
                if (intent.getBooleanExtra("openSettingsAfterLoad", false)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation requests", e);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    private void loadWeatherData() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        
        // Set default weather text while loading
        weatherTemp.setText("Loading weather...");
        weatherDesc.setText("");
        
        // Add a timeout handler in case the API call takes too long
        Handler timeoutHandler = new Handler();
        Runnable timeoutRunnable = () -> {
            weatherTemp.setText("Weather data unavailable");
            weatherDesc.setText("Check your connection");
            weatherIcon.setImageResource(R.drawable.ic_menu);
        };
        timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10 second timeout
        
        apiService.getWeather().enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                // Cancel timeout handler
                timeoutHandler.removeCallbacks(timeoutRunnable);
                
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecast wf = response.body();
                    updateWeatherUI(wf);
                } else {
                    weatherTemp.setText("Weather data unavailable");
                    weatherDesc.setText("Error: " + response.code());
                    weatherIcon.setImageResource(R.drawable.ic_menu);
                }
            }
            
            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                // Cancel timeout handler
                timeoutHandler.removeCallbacks(timeoutRunnable);
                
                Log.e(TAG, "Failed to load weather data", t);
                weatherTemp.setText("Weather data unavailable");
                weatherDesc.setText("Network error");
                weatherIcon.setImageResource(R.drawable.ic_menu);
            }
        });
    }

    private void updateWeatherUI(WeatherForecast wf) {
        if (wf == null) {
            weatherTemp.setText("Weather data unavailable");
            weatherDesc.setText("");
            weatherIcon.setImageResource(R.drawable.ic_menu);
            return;
        }
        
        try {
            // Icon handling with null safety
            String icon = wf.getIcon();
            if (icon != null) {
                weatherIcon.setImageResource(WeatherIconUtils.getWeatherIconResourceId(this, icon));
            } else {
                weatherIcon.setImageResource(R.drawable.ic_menu);
            }
            
            // Temperature formatting with default if needed
            String description = wf.getDescription();
            float temperature = (float) wf.getTemperature();
            weatherTemp.setText(String.format(Locale.getDefault(), "%.1f°C", temperature));
            
            // Description with null check
            weatherDesc.setText(description != null ? description : "");
        } catch (Exception e) {
            Log.e(TAG, "Error updating weather UI", e);
            weatherTemp.setText("Weather data unavailable");
            weatherDesc.setText("Error parsing data");
            weatherIcon.setImageResource(R.drawable.ic_menu);
        }
    }

    private void loadNewsData() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getNews().enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateNewsUI(response.body());
                } else {
                    showNewsError();
                }
            }
            
            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Log.e(TAG, "Failed to load news data", t);
                showNewsError();
            }
        });
    }

    private void updateNewsUI(List<News> newsList) {
        // Clear existing content
        newsContainer.removeAllViews();
        
        // Check for null or empty list
        if (newsList == null || newsList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No news available at the moment");
            emptyView.setPadding(16, 16, 16, 16);
            newsContainer.addView(emptyView);
            return;
        }
        
        // For each news item, inflate a view and add it to the container
        LayoutInflater inflater = getLayoutInflater();
        for (News news : newsList) {
            // Skip null news items
            if (news == null) continue;
            
            View newsItem = inflater.inflate(R.layout.item_news, newsContainer, false);
            
            // Set title with null check
            TextView titleView = newsItem.findViewById(R.id.newsTitle);
            String title = news.getTitle();
            titleView.setText(title != null ? title : "");
            
            // Set date with null check
            TextView dateView = newsItem.findViewById(R.id.newsDate);
            String date = news.getDate();
            if (date != null) {
                dateView.setText(date);
            } else {
                dateView.setVisibility(View.GONE);
            }
            
            // Set click listener for link
            final String link = news.getLink();
            if (link != null && !link.isEmpty()) {
                newsItem.setOnClickListener(v -> {
                    // Open the link when clicked
                    // ...
                });
            }
            
            newsContainer.addView(newsItem);
        }
    }

    private void showNewsError() {
        newsContainer.removeAllViews();
        TextView errorView = new TextView(this);
        errorView.setText("Unable to load news");
        errorView.setPadding(16, 16, 16, 16);
        newsContainer.addView(errorView);
    }

    private void setupClickListeners() {
        try {
            // Set up Emergency Alerts button
            Button emergencyAlertsButton = findViewById(R.id.emergencyAlertsButton);
            if (emergencyAlertsButton != null) {
                Log.d(TAG, "Found emergencyAlertsButton, setting click listener");
                
                emergencyAlertsButton.setOnClickListener(v -> {
                    Log.d(TAG, "Emergency Alerts button clicked, navigating to EmergencyAlertsActivity");
                    
                    try {
                        Intent intent = new Intent(HomeActivity.this, EmergencyAlertsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to EmergencyAlertsActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "Cannot open emergency alerts page: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "Could not find emergencyAlertsButton - make sure it exists in activity_home.xml");
            }
            
            // Add Activity Selection button
            Button activitySelectionButton = findViewById(R.id.activitySelectionButton);
            if (activitySelectionButton != null) {
                activitySelectionButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(HomeActivity.this, EventActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to EventActivity: " + e.getMessage());
                        Toast.makeText(this, "Cannot open activity selection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            // Add Club Activities button
            Button clubActivitiesButton = findViewById(R.id.clubActivitiesButton);
            if (clubActivitiesButton != null) {
                clubActivitiesButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(HomeActivity.this, ClubActivitiesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to ClubActivitiesActivity: " + e.getMessage());
                        Toast.makeText(this, "Cannot open club activities", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
        
        // Make navigation drawer menu items work by properly setting up the drawer
        setupNavigationDrawer();
    }
}