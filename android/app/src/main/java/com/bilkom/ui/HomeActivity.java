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
import com.bilkom.utils.WeatherIconUtils;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private LinearLayout newsContainer;
    private TextView weatherDescription, temperatureText;
    private ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
      
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.contentFrame));

        initializeViews();
        fetchWeatherForecast();
        fetchLatestNews();
    }

    private void initializeViews() {
        newsContainer = findViewById(R.id.newsContainer);
        weatherDescription = findViewById(R.id.weatherDescription);
        temperatureText = findViewById(R.id.temperatureText);
        weatherIcon = findViewById(R.id.weatherIcon);
    }

    private void fetchWeatherForecast() {
        RetrofitClient.getInstance().getApiService().getWeatherForecast()
            .enqueue(new Callback<WeatherForecast>() {
                @Override
                public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherForecast wf = response.body();
                        
                        // Update weather description
                        weatherDescription.setText(wf.getDescription());
                        
                        // Update temperature
                        temperatureText.setText(String.format("%.1f°C", wf.getTemperature()));
                        
                        // Update weather icon using WeatherIconUtils
                        int iconResourceId = WeatherIconUtils.getWeatherIconResourceId(HomeActivity.this, wf.getIcon());
                        weatherIcon.setImageResource(iconResourceId);
                    }
                }
                @Override
                public void onFailure(Call<WeatherForecast> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Failed to load weather", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HomeActivity.this, "Failed to load news", Toast.LENGTH_SHORT).show();
                }
            });
    }
}