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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

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
        
        // Try-catch to handle potential inflation issues
        try {
            View contentFrame = findViewById(R.id.contentFrame);
            if (contentFrame instanceof FrameLayout) {
                getLayoutInflater().inflate(R.layout.activity_home, (FrameLayout)contentFrame);
            } else {
                Log.w(TAG, "contentFrame not found or not a FrameLayout");
                setContentView(R.layout.activity_home);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout", e);
            setContentView(R.layout.activity_home);
        }
        
        setupNavigationDrawer();
        
        // Initialize views
        weatherTemp = findViewById(R.id.weatherTemp);
        weatherDesc = findViewById(R.id.weatherDesc);
        weatherIcon = findViewById(R.id.weatherIcon);
        newsContainer = findViewById(R.id.newsContainer);
        
        if (weatherTemp == null || weatherDesc == null || weatherIcon == null || newsContainer == null) {
            Log.e(TAG, "Failed to initialize views");
            return;
        }
        
        // Load data
        loadWeatherData();
        loadNewsData();
        
        // Setup clickable areas if needed
        setupClickListeners();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    private void loadWeatherData() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getWeather().enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecast wf = response.body();
                    updateWeatherUI(wf);
                } else {
                    weatherTemp.setText("Weather data unavailable");
                }
            }
            
            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e(TAG, "Failed to load weather data", t);
                weatherTemp.setText("Weather data unavailable");
            }
        });
    }

    private void updateWeatherUI(WeatherForecast wf) {
        if (wf == null) {
            weatherTemp.setText("Weather data unavailable");
            return;
        }
        
        try {
            // Icon handling with null safety
            String icon = wf.getIcon();
            weatherIcon.setImageResource(WeatherIconUtils.getWeatherIconResourceId(this, icon));
            
            // Temperature formatting with default if needed
            String description = wf.getDescription();
            float temperature = (float) wf.getTemperature();
            weatherTemp.setText(String.format(Locale.getDefault(), "%.1f°C", temperature));
            
            // Description with null check
            weatherDesc.setText(description != null ? description : "");
        } catch (Exception e) {
            Log.e(TAG, "Error updating weather UI", e);
            weatherTemp.setText("Weather data unavailable");
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
            
            // Set click listener with null check for link
            final String link = news.getLink();
            if (link != null && !link.isEmpty()) {
                newsItem.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening link: " + link, e);
                        Toast.makeText(HomeActivity.this, "Cannot open link", Toast.LENGTH_SHORT).show();
                    }
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
        // Make weather card clickable to show detailed forecast
        CardView weatherCard = findViewById(R.id.weatherCard);
        if (weatherCard != null) {
            weatherCard.setOnClickListener(v -> {
                Toast.makeText(this, "Weather details coming soon", Toast.LENGTH_SHORT).show();
                // Here you could start a WeatherDetailActivity
            });
        }
        
        // Add button to navigate to emergency alerts
        View.OnClickListener alertsListener = v -> {
            try {
                // Try to navigate to the EventActivity (where emergency alerts are shown)
                try {
                    Class<?> eventsClass = Class.forName("com.bilkom.ui.EventActivity");
                    startActivity(new Intent(this, eventsClass));
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "EventActivity class not found", e);
                    Toast.makeText(this, "Events feature coming soon", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to emergency alerts", e);
                Toast.makeText(this, "Cannot open emergency alerts", Toast.LENGTH_SHORT).show();
            }
        };
        
        // Find a view to attach the emergency alerts click listener
        LinearLayout newsContainer = findViewById(R.id.newsContainer);
        if (newsContainer != null && newsContainer.getChildCount() > 0) {
            // Add a "View Emergency Alerts" button at the top of news
            Button alertsButton = new Button(this);
            alertsButton.setText("View Emergency Alerts");
            alertsButton.setOnClickListener(alertsListener);
            
            // Add the button at the top of the news container
            newsContainer.addView(alertsButton, 0);
        }
        
        // Add Activity Selection button
        Button activitySelectionButton = findViewById(R.id.activitySelectionButton);
        if (activitySelectionButton != null) {
            activitySelectionButton.setOnClickListener(v -> {
                try {
                    // Navigate to EventActivity
                    try {
                        Class<?> eventsClass = Class.forName("com.bilkom.ui.EventActivity");
                        startActivity(new Intent(this, eventsClass));
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "EventActivity class not found", e);
                        Toast.makeText(this, "Activity Selection coming soon", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to activity selection", e);
                    Toast.makeText(this, "Cannot open activity selection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Add Club Activities button
        Button clubActivitiesButton = findViewById(R.id.clubActivitiesButton);
        if (clubActivitiesButton != null) {
            clubActivitiesButton.setOnClickListener(v -> {
                try {
                    // Navigate to ClubActivitiesActivity
                    try {
                        Class<?> clubsClass = Class.forName("com.bilkom.ui.ClubActivitiesActivity");
                        startActivity(new Intent(this, clubsClass));
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "ClubActivitiesActivity class not found", e);
                        Toast.makeText(this, "Club Activities coming soon", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to club activities", e);
                    Toast.makeText(this, "Cannot open club activities", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Make navigation drawer menu items work by properly setting up the drawer
        setupNavigationDrawer();
    }
}