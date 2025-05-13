// this is a pojo class for the weather forecast 
// it is used to store the weather forecast data from the server using
// temperature, description, humidity, windSpeed, iconCode and location
package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for weather forecast information.
 * Designed to match the backend WeatherDto.
 * Updated to match the backend WeatherDto.
 * 
 * @author Mert Uzun and Elif Bozkurt
 * @version 1.0
 * @since 2025-05-09
 */
public class WeatherForecast {
    @SerializedName("temperature")
    private float temperature;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("feelsLike")
    private double feelsLike;
    
    @SerializedName("humidity")
    private int humidity;
    
    @SerializedName("windSpeed")
    private double windSpeed;
    
    @SerializedName("windDirection")
    private String windDirection;
    
    @SerializedName("timestamp")
    private String timestamp;

    public WeatherForecast() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Get icon URL based on the icon code
     * @return Full URL to the weather icon
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
    }
} 