package com.bilkom.android.network.models;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class EmergencyAlert {
    @SerializedName("alertId")
    private Long alertId;
    
    @SerializedName("alertType")
    private String alertType;
    
    @SerializedName("bloodType")
    private String bloodType;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("alertDescription")
    private String alertDescription;
    
    @SerializedName("alertDate")
    private Timestamp alertDate;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    // Getters and Setters
    public Long getAlertId() {
        return alertId;
    }
    
    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAlertDescription() {
        return alertDescription;
    }
    
    public void setAlertDescription(String alertDescription) {
        this.alertDescription = alertDescription;
    }
    
    public Timestamp getAlertDate() {
        return alertDate;
    }
    
    public void setAlertDate(Timestamp alertDate) {
        this.alertDate = alertDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
} 