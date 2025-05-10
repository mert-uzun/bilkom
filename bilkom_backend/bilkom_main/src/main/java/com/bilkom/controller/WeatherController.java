package com.bilkom.controller;

import com.bilkom.dto.WeatherDto;
import com.bilkom.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * WeatherController is responsible for handling HTTP requests related to
 * weather information.
 * It provides an endpoint for fetching the current weather data.
 *
 * @author Elif Bozkurt
 * @version 1.0
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * Fetches the current weather data.
     * 
     * @return WeatherDto object containing weather details
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    @GetMapping
    public WeatherDto getWeather() {
        return weatherService.getWeather();
    }
}
