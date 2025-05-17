// this is the main activity class for the main page
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.bilkom.R;
import com.bilkom.model.News;
import com.bilkom.model.WeatherForecast;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.bilkom.utils.WeatherIconUtils;

import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SecureStorage secureStorage;
    
    // Weather UI elements
    private ImageView weatherIcon;
    private TextView weatherDescription;
    private TextView temperatureText;
    private TextView humidityText;
    private TextView windText;
    
    // News container
    private LinearLayout newsContainer;
    
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
            
            // Initialize weather views
            initializeWeatherViews();
            
            // Initialize news container
            newsContainer = findViewById(R.id.newsContainer);
            
            // Set up activity buttons
            setupActivityButtons();
            
            // Load weather and news data
            loadWeatherData();
            loadNewsData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initializeWeatherViews() {
        try {
            View weatherContainer = findViewById(R.id.weatherContainer);
            
            if (weatherContainer != null) {
                weatherIcon = weatherContainer.findViewById(R.id.weatherIcon);
                weatherDescription = weatherContainer.findViewById(R.id.weatherDescription);
                temperatureText = weatherContainer.findViewById(R.id.temperatureText);
                humidityText = weatherContainer.findViewById(R.id.humidityText);
                windText = weatherContainer.findViewById(R.id.windText);
                
                // Set default values
                if (weatherDescription != null) weatherDescription.setText("Loading weather...");
                if (temperatureText != null) temperatureText.setText("--°C");
                if (humidityText != null) humidityText.setText("--%");
                if (windText != null) windText.setText("-- km/h");
                if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.weather);
            } else {
                Log.e(TAG, "Weather container not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing weather views", e);
        }
    }
    
    private void loadWeatherData() {
        try {
            ApiService apiService = RetrofitClient.getInstance().getApiService();
            
            // Set default weather text while loading
            if (weatherDescription != null) weatherDescription.setText("Loading weather...");
            
            // Add a timeout handler in case the API call takes too long
            Handler timeoutHandler = new Handler();
            Runnable timeoutRunnable = () -> {
                if (weatherDescription != null) weatherDescription.setText("Weather data unavailable");
                if (temperatureText != null) temperatureText.setText("--°C");
                if (humidityText != null) humidityText.setText("--%");
                if (windText != null) windText.setText("-- km/h");
                if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.ic_menu);
            };
            timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10 second timeout
            
            apiService.getWeather().enqueue(new Callback<WeatherForecast>() {
                @Override
                public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                    // Cancel timeout handler
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherForecast wf = response.body();
                        Log.d(TAG, "Weather data received: " + wf.getDescription() + ", " + 
                              wf.getTemperature() + "°C, Humidity: " + wf.getHumidity() + "%");
                        updateWeatherUI(wf);
                    } else {
                        if (weatherDescription != null) weatherDescription.setText("Weather data unavailable");
                        if (temperatureText != null) temperatureText.setText("--°C");
                        if (humidityText != null) humidityText.setText("--%");
                        if (windText != null) windText.setText("-- km/h");
                        if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.ic_menu);
                        Log.e(TAG, "Weather API error: " + (response != null ? response.code() : "null response"));
                    }
                }
                
                @Override
                public void onFailure(Call<WeatherForecast> call, Throwable t) {
                    // Cancel timeout handler
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    Log.e(TAG, "Failed to load weather data", t);
                    if (weatherDescription != null) weatherDescription.setText("Weather data unavailable");
                    if (temperatureText != null) temperatureText.setText("--°C");
                    if (humidityText != null) humidityText.setText("--%");
                    if (windText != null) windText.setText("-- km/h");
                    if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.ic_menu);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading weather data", e);
        }
    }

    private void updateWeatherUI(WeatherForecast wf) {
        if (wf == null) {
            if (weatherDescription != null) weatherDescription.setText("Weather data unavailable");
            if (temperatureText != null) temperatureText.setText("--°C");
            if (humidityText != null) humidityText.setText("--%");
            if (windText != null) windText.setText("-- km/h");
            if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.ic_menu);
            return;
        }
        
        try {
            // Icon handling with null safety
            if (weatherIcon != null) {
                weatherIcon.setImageResource(R.drawable.weather);
            }
            
            // Description
            String description = wf.getDescription();
            if (weatherDescription != null) {
                weatherDescription.setText(description != null ? description : "Unknown weather");
            }
            
            // Temperature formatting - fixed to properly handle temperature value
            if (temperatureText != null) {
                double temperature = wf.getTemperature();
                temperatureText.setText(String.format(Locale.getDefault(), "%.1f°C", temperature));
                Log.d(TAG, "Temperature set to: " + temperature + "°C");
            }
            
            // Humidity
            if (humidityText != null) {
                int humidity = wf.getHumidity();
                humidityText.setText(humidity + "%");
            }
            
            // Wind speed
            if (windText != null) {
                double windSpeed = wf.getWindSpeed();
                windText.setText(String.format(Locale.getDefault(), "%.1f km/h", windSpeed));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating weather UI", e);
            if (weatherDescription != null) weatherDescription.setText("Error parsing weather data");
            if (temperatureText != null) temperatureText.setText("--°C");
            if (humidityText != null) humidityText.setText("--%");
            if (windText != null) windText.setText("-- km/h");
            if (weatherIcon != null) weatherIcon.setImageResource(R.drawable.ic_menu);
        }
    }
    
    private void loadNewsData() {
        if (newsContainer == null) return;
        
        try {
            // Clear existing content and show loading state
            newsContainer.removeAllViews();
            TextView loadingView = new TextView(this);
            loadingView.setText("Loading news...");
            loadingView.setPadding(16, 16, 16, 16);
            loadingView.setTextColor(getResources().getColor(R.color.white));
            newsContainer.addView(loadingView);
            
            // Add a timeout handler in case the API call takes too long
            Handler timeoutHandler = new Handler();
            Runnable timeoutRunnable = () -> {
                showNewsError("Request timed out. Check your connection.");
            };
            timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10 second timeout
            
            ApiService apiService = RetrofitClient.getInstance().getApiService();
            apiService.getNews().enqueue(new Callback<List<News>>() {
                @Override
                public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                    // Cancel timeout handler
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        List<News> newsList = response.body();
                        Log.d(TAG, "News data received: " + newsList.size() + " items");
                        updateNewsUI(newsList);
                    } else {
                        Log.e(TAG, "News API error: " + (response != null ? response.code() : "null response"));
                        showNewsError("Failed to load news data");
                    }
                }
                
                @Override
                public void onFailure(Call<List<News>> call, Throwable t) {
                    // Cancel timeout handler
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    Log.e(TAG, "Failed to load news data", t);
                    showNewsError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading news data", e);
            showNewsError("Error: " + e.getMessage());
        }
    }

    private void updateNewsUI(List<News> newsList) {
        if (newsContainer == null) return;
        
        // Clear existing content
        newsContainer.removeAllViews();
        
        // Check for null or empty list
        if (newsList == null || newsList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No news available at the moment");
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setTextColor(getResources().getColor(R.color.white));
            newsContainer.addView(emptyView);
            return;
        }
        
        // For each news item, inflate a view and add it to the container (limit to 3 items)
        LayoutInflater inflater = getLayoutInflater();
        int count = 0;
        for (News news : newsList) {
            if (count >= 3) break; // Limit to 3 news items for the main screen
            
            // Skip null news items
            if (news == null) continue;
            
            try {
                View newsItem = inflater.inflate(R.layout.item_news, newsContainer, false);
                
                // Set title with null check
                TextView titleView = newsItem.findViewById(R.id.newsTitle);
                String title = news.getTitle();
                if (titleView != null) {
                    titleView.setText(title != null ? title : "");
                }
                
                // Set date with null check
                TextView dateView = newsItem.findViewById(R.id.newsDate);
                String date = news.getDate();
                if (dateView != null) {
                    if (date != null) {
                        dateView.setText(date);
                    } else {
                        dateView.setVisibility(View.GONE);
                    }
                }
                
                // Set click listener for link or details
                final String link = news.getLink();
                if (link != null && !link.isEmpty()) {
                    newsItem.setOnClickListener(v -> {
                        try {
                            // Open the link in browser
                            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(link));
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error opening news link", e);
                            Toast.makeText(MainActivity.this, "Cannot open link", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                newsContainer.addView(newsItem);
                count++;
            } catch (Exception e) {
                Log.e(TAG, "Error adding news item", e);
            }
        }
        
        // Add a "View all news" button if there are more than 3 items
        if (newsList.size() > 3) {
            Button viewAllButton = new Button(this);
            viewAllButton.setText("View All News");
            viewAllButton.setBackgroundTintList(getResources().getColorStateList(R.color.bilkom_blue));
            viewAllButton.setTextColor(getResources().getColor(R.color.white));
            viewAllButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to news page", e);
                    Toast.makeText(this, "News feature coming soon", Toast.LENGTH_SHORT).show();
                }
            });
            newsContainer.addView(viewAllButton);
        }
    }

    private void showNewsError(String message) {
        if (newsContainer == null) return;
        
        newsContainer.removeAllViews();
        TextView errorView = new TextView(this);
        errorView.setText("Unable to load news: " + message);
        errorView.setPadding(16, 16, 16, 16);
        errorView.setTextColor(getResources().getColor(R.color.white));
        newsContainer.addView(errorView);
    }
    
    private void setupActivityButtons() {
        try {
            // Activity Selection button
            Button activitySelectionButton = findViewById(R.id.activitySelectionButton);
            if (activitySelectionButton != null) {
                activitySelectionButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(MainActivity.this, EventActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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
                        Intent intent = new Intent(MainActivity.this, EmergencyAlertsActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Navigating to EmergencyAlertsActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to emergency alerts", e);
                        Toast.makeText(this, "Cannot open emergency alerts", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up activity buttons", e);
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
                    else if (itemId == R.id.menu_settings) {
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), "com.bilkom.ui.SettingsActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Cannot open settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        super.onPause();
        // Clean up any resources
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up any resources
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources
    }
} 