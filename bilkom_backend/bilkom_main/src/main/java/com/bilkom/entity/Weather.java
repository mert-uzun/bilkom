package com.bilkom.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private double temperature;

    private String icon;

    @Column(name = "retrieved_at", nullable = false)
    private LocalDateTime retrievedAt;

    public Weather() {}

    public Weather(String description, double temperature, String icon, LocalDateTime retrievedAt) {
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
        this.retrievedAt = retrievedAt;
    }

    public Long getId() {
        return id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }
}
