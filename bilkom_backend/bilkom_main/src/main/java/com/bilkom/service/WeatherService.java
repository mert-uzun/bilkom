package com.bilkom.service;

import com.bilkom.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherDto getWeather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Bilkent,tr&units=metric&appid=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject obj = new JSONObject(response);
        String description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String icon = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
        double temperature = obj.getJSONObject("main").getDouble("temp");

        return new WeatherDto(description, temperature, icon);
    }
}
