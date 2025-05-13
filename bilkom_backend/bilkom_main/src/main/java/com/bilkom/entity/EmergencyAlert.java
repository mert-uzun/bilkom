package com.bilkom.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * EmergencyAlert is an entity class representing an emergency alert in the system.
 * It contains information about the alert such as subject, content, blood type, phone number, and sent date.
 * The class also includes methods to extract blood type and phone number from the content.
 *
 * @author Elif Bozkurt
 * @version 1.0
 */
@Entity
@Table(name = "emergency_alerts")
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "subject", nullable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "blood_type", columnDefinition = "VARCHAR(20)")
    private String bloodType;

    @Column(name = "phone_number", columnDefinition = "VARCHAR(20)")
    private String phoneNumber;

    @Column(name = "sent_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public EmergencyAlert() {}

    public EmergencyAlert(String subject, String content, Date sentDate) {
        this.subject = subject;
        this.content = content;
        this.sentDate = sentDate;
        this.bloodType = extractBloodType(content);
        this.phoneNumber = extractPhoneNumber(content);
        this.isActive = true;
    }

    /**
     * Extracts the blood type from the content using regular expressions.
     * The method looks for various patterns that represent blood types and returns the first match found.
     *
     * @param content The content from which to extract the blood type
     * @return The extracted blood type or an empty string if not found
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    private String extractBloodType(String content) {
        if (content == null || content.isEmpty()) return "";
    
        String upper = content.toUpperCase(java.util.Locale.forLanguageTag("tr-TR"));
    
        String[] patterns = {
            "(A|B|AB|O|0)\\s*RH\\s*\\(\\s*([+-])\\s*\\)",                   
            "(A|B|AB|O|0)\\s*RH\\s*([+-])",                                 
            "(A|B|AB|O|0)RH([+-])",                                         
            "(A|B|AB|O|0)([+-])",                                           
            "(A|B|AB|O|0)\\s*([+-])\\s*KAN",                                
            "(A|B|AB|O|0)\\s*(POZITIF|NEGATIF|POZİTİF|NEGATİF)",            
            "(A|B|AB|O|0)\\s*RH\\s*(POZITIF|NEGATIF|POZİTİF|NEGATİF)",       
            "KAN\\s*GRUBU\\s*[:\\-]?\\s*(A|B|AB|O|0)\\s*RH\\s*\\(?\\s*([+-])\\s*\\)?", 
            "KAN\\s*GRUBU\\s*[:\\-]?\\s*(A|B|AB|O|0)\\s*([+-])"             
        };
    
        for (String regex : patterns) {
            var pattern = java.util.regex.Pattern.compile(regex);
            var matcher = pattern.matcher(upper);
            if (matcher.find()) {
                String blood = matcher.group(1);
                String sign = matcher.group(2);
    
                sign = sign.replace('İ', 'I').toUpperCase();
                if ("POZITIF".equals(sign)) sign = "+";
                else if ("NEGATIF".equals(sign)) sign = "-";
    
                return blood + " Rh (" + sign + ")";
            }
        }
        return "";
    }

    /**
     * Extracts the phone number from the content using regular expressions.
     * The method looks for various patterns that represent phone numbers and returns the first match found.
     *
     * @param content The content from which to extract the phone number
     * @return The extracted phone number or an empty string if not found
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    private String extractPhoneNumber(String content) {
        if (content == null || content.isEmpty()) return "";

        String cleaned = content.replaceAll("\\s+", " ");
        String[] patterns = {
            "\\+\\d{1,3}[\\s\\-]?(\\(\\d{3}\\)|\\d{3})[\\s\\-]?\\d{3}[\\s\\-]?\\d{2,4}[\\s\\-]?\\d{2,4}",
            "0\\s?5\\d{2}[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}",
            "0\\d{10}",
            "\\+1\\s?\\(\\d{3}\\)\\s?\\d{3}[\\-\\s]?\\d{4}"
        };

        for (String regex : patterns) {
            var pattern = java.util.regex.Pattern.compile(regex);
            var matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        }
        return "";
    }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date sentDate) { this.sentDate = sentDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "EmergencyAlert{" +
                "alertId=" + alertId +
                ", subject='" + subject + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", sentDate=" + sentDate +
                ", isActive=" + isActive +
                '}';
    }
}