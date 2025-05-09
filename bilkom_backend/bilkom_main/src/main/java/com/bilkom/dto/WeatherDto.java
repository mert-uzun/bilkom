package com.bilkom.dto;

/**
 * WeatherDto is a Data Transfer Object (DTO) that represents weather information.
 * Contains current weather data for Bilkent/Ankara.
 * 
 * @author Elif Bozkurt
 * @version 2.0
 * @since 2025-05-09
 */
public class WeatherDto {
    private String description;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String windDirection;
    private String iconCode;
    private String location;
    private String timestamp;

    public WeatherDto() {}

    public WeatherDto(String description, double temperature, String iconCode) {
        this.description = description;
        this.temperature = temperature;
        this.iconCode = iconCode;
        this.location = "Bilkent, Ankara";
    }

    public WeatherDto(String description, double temperature, double feelsLike, int humidity,
                      double windSpeed, String windDirection, String iconCode, 
                      String location, String timestamp) {
        this.description = description;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.iconCode = iconCode;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
