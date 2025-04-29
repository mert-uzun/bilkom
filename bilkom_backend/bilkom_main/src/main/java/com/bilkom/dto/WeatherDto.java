package com.bilkom.dto;

/**
 * WeatherDto is a Data Transfer Object (DTO) that represents weather information.
 * It contains the description, temperature, and icon of the weather.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
public class WeatherDto {
    private String description;
    private double temperature;
    private String icon;

    public WeatherDto() {}

    public WeatherDto(String description, double temperature, String icon) {
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
