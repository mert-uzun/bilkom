package com.bilkom.controller;

import com.bilkom.dto.WeatherDto;
import com.bilkom.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public WeatherDto getWeather() {
        return weatherService.getWeather();
    }
}
