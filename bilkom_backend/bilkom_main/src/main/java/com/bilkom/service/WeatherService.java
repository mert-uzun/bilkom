package com.bilkom.service;

import com.bilkom.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * WeatherService is responsible for fetching weather data from the OpenWeatherMap API.
 * It retrieves the current weather information for Bilkent, Turkey.
 *
 * @author Elif Bozkurt and Mert Uzun
 * @version 2.0
 * @since 2025-05-09
 */ 
@Service
public class WeatherService {

    @Value("${openweathermap.api.key:1234567890abcdef}")
    private String apiKey;
    
    private static final String LOCATION = "Bilkent,Ankara,tr";
    private static final String UNITS = "metric";

    /**
     * Fetches current weather data for Bilkent, Ankara.
     * Results are cached for 30 minutes to avoid excessive API calls.
     *
     * @return WeatherDto containing current weather information
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    @Cacheable(value = "weatherCache", key = "'currentWeather'")
    public WeatherDto getWeather() {
        String url = String.format(
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&appid=%s",
            LOCATION, UNITS, apiKey
        );
        
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject jsonResponse = new JSONObject(response);
        
        // Extract basic weather information
        JSONObject weatherObj = jsonResponse.getJSONArray("weather").getJSONObject(0);
        String description = weatherObj.getString("description");
        String iconCode = weatherObj.getString("icon");
        
        // Extract main weather data
        JSONObject mainObj = jsonResponse.getJSONObject("main");
        double temperature = mainObj.getDouble("temp");
        double feelsLike = mainObj.getDouble("feels_like");
        int humidity = mainObj.getInt("humidity");
        
        // Extract wind data
        JSONObject windObj = jsonResponse.getJSONObject("wind");
        double windSpeed = windObj.getDouble("speed");
        String windDirection = getWindDirection(windObj.optDouble("deg", 0));
        
        // Extract location
        String location = jsonResponse.getString("name") + ", Ankara";
        
        // Format current timestamp
        long timestamp = jsonResponse.getLong("dt") * 1000; // Convert to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        String formattedTime = sdf.format(new Date(timestamp));
        
        return new WeatherDto(
            description,
            temperature,
            feelsLike,
            humidity,
            windSpeed,
            windDirection,
            iconCode,
            location,
            formattedTime
        );
    }
    
    /**
     * Converts wind degrees to cardinal direction
     * 
     * @param degrees Wind direction in degrees
     * @return Cardinal direction as string (N, NE, E, etc.)
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    private String getWindDirection(double degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        return directions[(int) Math.round((degrees % 360) / 45)];
    }
}
