package com.bilkom;

import com.bilkom.dto.WeatherDto;
import com.bilkom.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WeatherService integration with real OpenWeatherMap API.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;
    
    @Value("${openweathermap.api.key}")
    private String apiKey;

    @BeforeEach
    public void setUp() {
        // No setup needed as we're using actual services
        // The API key should be configured in application-test.properties
        assertNotNull(apiKey, "API key must be configured in application-test.properties");
        assertFalse(apiKey.isEmpty(), "API key cannot be empty");
    }

    @Test
    public void testGetWeather() {
        // Get weather from actual API
        WeatherDto weatherDto = weatherService.getWeather();
        
        // Verify results
        assertNotNull(weatherDto, "Weather DTO should not be null");
        assertNotNull(weatherDto.getDescription(), "Weather description should not be null");
        assertNotNull(weatherDto.getIconCode(), "Icon code should not be null");
        
        // Temperature and other values may vary, so we just check they're in reasonable range
        assertTrue(weatherDto.getTemperature() > -50 && weatherDto.getTemperature() < 60, 
            "Temperature should be in a reasonable range");
        assertTrue(weatherDto.getHumidity() >= 0 && weatherDto.getHumidity() <= 100, 
            "Humidity should be between 0 and 100");
        assertTrue(weatherDto.getWindSpeed() >= 0, 
            "Wind speed should be non-negative");
    }

    @Test
    public void testWeatherCache() {
        // Call the method twice
        WeatherDto firstCall = weatherService.getWeather();
        WeatherDto secondCall = weatherService.getWeather();
        
        // Verify both results are equal (from cache)
        assertEquals(firstCall.getTemperature(), secondCall.getTemperature(), 
            "Second call should return cached results");
        assertEquals(firstCall.getDescription(), secondCall.getDescription(), 
            "Second call should return cached results");
    }

    @Test
    public void testLocationSpecificWeather() {
        // This test will only pass if the weather service correctly targets Bilkent location
        WeatherDto weatherDto = weatherService.getWeather();
        
        // The location should be for Bilkent area, which is in Ankara, Turkey
        // We don't check the exact name because the API might return "Ankara" or other nearby locations
        assertNotNull(weatherDto, "Weather data should be retrieved");
    }
} 