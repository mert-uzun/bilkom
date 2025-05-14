package com.bilkom.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bilkom.entity.EmergencyAlert;
import com.bilkom.entity.User;
import com.bilkom.repository.EmergencyAlertRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import java.time.Instant;
import java.util.Date;
import java.time.temporal.ChronoUnit;

/**
 * Service for fetching and processing emergency alerts from Gmail.
 * It connects to the Gmail IMAP server, retrieves messages, and sends notifications to users based on blood type.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
@Service
public class EmergencyAlertService {
    private static final Logger log = LoggerFactory.getLogger(EmergencyAlertService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private EmergencyAlertRepository emergencyAlertRepository;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${spring.mail.password}")
    private String appPassword;
    
    /**
     * Gets all emergency alerts from the repository.
     * 
     * @return List of all EmergencyAlert objects
     */
    public List<EmergencyAlert> getAllEmergencyAlerts() {
        return emergencyAlertRepository.findAll();
    }
    
    /**
     * Gets all active emergency alerts from the repository.
     * 
     * @return List of active EmergencyAlert objects
     */
    public List<EmergencyAlert> getActiveEmergencyAlerts() {
        return emergencyAlertRepository.findByIsActiveTrue();
    }
    
    /**
     * Creates a new emergency alert and notifies matching users.
     * 
     * @param subject The alert subject
     * @param content The alert content
     * @param bloodType The required blood type
     * @return The created EmergencyAlert object
     */
    public EmergencyAlert createEmergencyAlert(String subject, String content, String bloodType) {
        EmergencyAlert alert = new EmergencyAlert();
        alert.setSubject(subject);
        alert.setContent(content);
        alert.setBloodType(bloodType);
        alert.setActive(true);
        alert.setSentDate(new Date());
        
        // Save the alert
        alert = emergencyAlertRepository.save(alert);
        
        // Notify users with matching blood type
        if (bloodType != null && !bloodType.isEmpty()) {
            List<User> matchingUsers = userService.getUsersByBloodType(bloodType);
            for (User user : matchingUsers) {
                if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                    notificationService.sendFcm(
                            user.getFcmToken(),
                            bloodType + " BLOOD NEEDED",
                            content + "\n\n");
                    log.info("Notified {} for blood type {}", user.getEmail(), bloodType);
                }
            }
        }
        
        return alert;
    }

    /**
     * Fetches emergency alerts from Gmail and sends notifications to users based on blood type.
     * 
     * @return List of EmergencyAlert objects
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    public List<EmergencyAlert> fetchEmergencyAlerts() {
        List<EmergencyAlert> result = new ArrayList<>();

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", email, appPassword);

            Folder inbox = store.getFolder("[Gmail]/All Mail");
            inbox.open(Folder.READ_ONLY);

            int count = inbox.getMessageCount();
            int start = Math.max(1, count - 500);
            Message[] messages = inbox.getMessages(start, count);

            Date cutoff = Date.from(Instant.now().minus(48, ChronoUnit.HOURS));

            for (Message message : messages) {
                Date sentDate = message.getSentDate();
                if (sentDate == null || sentDate.before(cutoff)) {
                    continue;
                }

                String subject = message.getSubject();
                if (subject != null && subject.contains("ACİL KAN İHTİYACI")) {
                    String content = getTextFromMessage(message);
                    EmergencyAlert emergencyAlert = new EmergencyAlert(subject, content, sentDate);
                    
                    // Save the alert to the database
                    emergencyAlert = emergencyAlertRepository.save(emergencyAlert);
                    result.add(emergencyAlert);

                    String bloodType = emergencyAlert.getBloodType();
                    if (bloodType != null && !bloodType.isEmpty()) {
                        List<User> matchingUsers = userService.getUsersByBloodType(bloodType);
                        for (User user : matchingUsers) {
                            if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                                notificationService.sendFcm(
                                        user.getFcmToken(),
                                        bloodType + " BLOOD NEEDED",
                                        "" + emergencyAlert.getContent() + "\n\n");
                                log.info("Notified {} for blood type {}", user.getEmail(), bloodType);
                            }
                        }
                    }
                }
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            log.error("Error while fetching alerts: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * Scheduled method to check for new emergency alerts every minute.
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    @Scheduled(fixedRate = 60000)
    public void scheduledMailCheck() {
        List<EmergencyAlert> newAlerts = fetchEmergencyAlerts();
        log.info("Checked mail at {} — found {} emergency alerts", java.time.LocalTime.now(), newAlerts.size());
    }

    /**
     * Extracts text content from a message, handling both plain text and multipart messages.
     * 
     * @param message The message from which to extract text
     * @return The extracted text content
     * @throws Exception if an error occurs while extracting the content
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent());
                }
            }
            return result.toString().replaceAll("\r\n", "");
        }
        return "";
    }
}