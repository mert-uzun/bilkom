// this is the home activity class for the home page
// it is used to store the home page content
// it extends the BaseActivity class to show the menu 
package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import com.bilkom.model.EmergencyAlert;
import com.bilkom.model.News;
import com.bilkom.model.WeatherForecast;
import com.bilkom.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

        findViewById(R.id.emergencyAlertsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, EmergencyAlertsActivity.class));
        });
        findViewById(R.id.activitySelectionButton).setOnClickListener(v -> {
            Toast.makeText(this, "Activity Selection coming soon!", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.clubActivitiesButton).setOnClickListener(v -> {
            Toast.makeText(this, "Club Activities coming soon!", Toast.LENGTH_SHORT).show();
        });

        fetchLatestEmergencyAlert();
        fetchWeatherForecast();
        fetchLatestNews();
    }

    private void fetchLatestEmergencyAlert() {
        RetrofitClient.getInstance().getApiService().getEmergencyAlerts(null)
            .enqueue(new Callback<List<EmergencyAlert>>() {
                @Override
                public void onResponse(Call<List<EmergencyAlert>> call, Response<List<EmergencyAlert>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        EmergencyAlert alert = response.body().get(0); // latest alert
                        ((TextView) findViewById(R.id.alertTitle)).setText(alert.getSubject());
                        ((TextView) findViewById(R.id.alertMessage)).setText(alert.getContent());
                        if (alert.getPhoneNumber() != null && !alert.getPhoneNumber().isEmpty()) {
                            findViewById(R.id.contactAlertButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.contactAlertButton).setOnClickListener(v -> {
                                // Implement contact action, e.g., open dialer
                                Toast.makeText(HomeActivity.this, "Contact: " + alert.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            findViewById(R.id.contactAlertButton).setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<EmergencyAlert>> call, Throwable t) {
                    // Optionally show error
                }
            });
    }

    private void fetchWeatherForecast() {
        RetrofitClient.getInstance().getApiService().getWeatherForecast()
            .enqueue(new Callback<WeatherForecast>() {
                @Override
                public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherForecast wf = response.body();
                        
                        // Update weather description
                        TextView weatherDescription = findViewById(R.id.weatherDescription);
                        weatherDescription.setText(wf.getDescription());
                        
                        // Update temperature
                        TextView temperatureText = findViewById(R.id.temperatureText);
                        temperatureText.setText(String.format("%.1f°C", wf.getTemperature()));
                        
                        // Update humidity
                        TextView humidityText = findViewById(R.id.humidityText);
                        humidityText.setText(String.format("%d%%", wf.getHumidity()));
                        
                        // Update wind speed
                        TextView windText = findViewById(R.id.windText);
                        windText.setText(String.format("%.1f km/h", wf.getWindSpeed()));
                        
                        // Update weather icon based on description
                        ImageView weatherIcon = findViewById(R.id.weatherIcon);
                        String description = wf.getDescription().toLowerCase();
                        if (description.contains("rain") || description.contains("drizzle")) {
                            weatherIcon.setImageResource(R.drawable.ic_weather_rainy);
                        } else if (description.contains("cloud")) {
                            weatherIcon.setImageResource(R.drawable.ic_weather_cloudy);
                        } else if (description.contains("sun") || description.contains("clear")) {
                            weatherIcon.setImageResource(R.drawable.ic_weather_sunny);
                        } else if (description.contains("snow")) {
                            weatherIcon.setImageResource(R.drawable.ic_weather_snowy);
                        } else {
                            weatherIcon.setImageResource(R.drawable.ic_weather_cloudy);
                        }
                    }
                }
                @Override
                public void onFailure(Call<WeatherForecast> call, Throwable t) {
                    // Optionally show error
                }
            });
    }

    private void fetchLatestNews() {
        RetrofitClient.getInstance().getApiService().getLatestNews()
            .enqueue(new Callback<List<News>>() {
                @Override
                public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<News> newsList = response.body();
                        LinearLayout newsContainer = findViewById(R.id.newsContainer);
                        newsContainer.removeAllViews();
                        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                        int count = 0;
                        for (News news : newsList) {
                            if (count++ >= 10) break;
                            View newsItem = inflater.inflate(R.layout.item_news, newsContainer, false);
                            ((TextView) newsItem.findViewById(R.id.newsTitle)).setText(news.getTitle());
                            newsItem.setOnClickListener(v -> {
                                // Open news link in browser
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(android.net.Uri.parse(news.getLink()));
                                startActivity(intent);
                            });
                            newsContainer.addView(newsItem);
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<News>> call, Throwable t) {
                    // Optionally show error
                }
            });
    }
} 