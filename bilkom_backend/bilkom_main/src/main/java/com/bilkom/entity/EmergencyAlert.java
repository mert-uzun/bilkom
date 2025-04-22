package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.sql.Timestamp;

@Entity
@Table(name = "emergency_alerts")
public class EmergencyAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id", nullable = false, columnDefinition = "BIGINT")
    private Long alertId;

    @Column(name = "alert_type", nullable = false, columnDefinition = "VARCHAR(255)")
    private String alertType;

    @Column(name = "blood_type", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL DEFAULT 'Blood Infusion'")
    private String bloodType;

    @Column(name = "phone_number", nullable = false, columnDefinition = "VARCHAR(15)")
    private String phoneNumber;

    @Column(name = "alert_description", nullable = false, columnDefinition = "TEXT")
    private String alertDescription;

    @Column(name = "alert_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp alertDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Override
    public String toString() {
        return "EmergencyAlert{" + "alertId=" + alertId + ", alertType=" + alertType + ", bloodType=" + bloodType + ", phoneNumber=" + phoneNumber + ", alertDescription=" + alertDescription + ", alertDate=" + alertDate + ", isActive=" + isActive + '}';
    }

    // GETTERS AND SETTERS
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

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
