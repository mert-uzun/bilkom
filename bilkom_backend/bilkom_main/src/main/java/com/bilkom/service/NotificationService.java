package com.bilkom.service;

import com.bilkom.entity.User;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * NotificationService is responsible for sending notifications using Firebase Cloud Messaging (FCM).
 * It provides methods to send FCM messages with a specified token, title, and body.
 * 
 * @author Elif Bozkurt, Mert Uzun
 * @version 2.1
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    @Value("${firebase.service.account.path:src/main/resources/firebase-service-account.json}")
    private String serviceAccountPath;

    /**
     * Sends a notification using Firebase Cloud Messaging (FCM).
     * 
     * @param token The FCM token of the recipient device
     * @param title The title of the notification
     * @param body  The body of the notification
     * 
     * @author Elif Bozkurt
     * @version 2.0
     */
    public void sendFcm(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            log.warn("Attempted to send FCM notification with null or empty token");
            return;
        }
        
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new FileInputStream(serviceAccountPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
            googleCredentials.refreshIfExpired();
            String accessToken = googleCredentials.getAccessToken().getTokenValue();

            URI uri = new URI("https://fcm.googleapis.com/v1/projects/bilkom-11cc3/messages:send");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            String message = """
            {
              "message": {
                "token": "%s",
                "notification": {
                  "title": "%s",
                  "body": "%s"
                }
              }
            }
            """.formatted(token, title, body);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            log.info("FCM Response Code: " + responseCode);

        } catch (Exception e) {
            log.error("Error sending FCM notification: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Sends a notification to a specific user.
     * 
     * @param user  The user to send the notification to
     * @param title The title of the notification
     * @param body  The body of the notification
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendNotificationToUser(User user, String title, String body) {
        if (user == null) {
            log.warn("Attempted to send notification to null user");
            return;
        }
        
        String fcmToken = user.getFcmToken();
        if (fcmToken != null && !fcmToken.isEmpty()) {
            sendFcm(fcmToken, title, body);
            log.info("Sent notification to user: {}", user.getEmail());
        } else {
            log.warn("User {} has no FCM token", user.getEmail());
        }
    }
    
    /**
     * Sends a notification to multiple users.
     * 
     * @param users List of users to send the notification to
     * @param title The title of the notification
     * @param body  The body of the notification
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendNotificationToUsers(List<User> users, String title, String body) {
        if (users == null || users.isEmpty()) {
            log.warn("Attempted to send notification to empty user list");
            return;
        }
        
        // Process each user - optionally could be made parallel for large user lists
        for (User user : users) {
            sendNotificationToUser(user, title, body);
        }
        
        log.info("Sent notifications to {} users", users.size());
    }
    
    /**
     * Sends notifications to multiple users asynchronously using CompletableFuture.
     * This is useful for sending many notifications without blocking.
     * 
     * @param users List of users to send the notification to
     * @param title The title of the notification
     * @param body  The body of the notification
     * @return CompletableFuture that completes when all notifications are sent
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public CompletableFuture<Void> sendNotificationToUsersAsync(List<User> users, String title, String body) {
        return CompletableFuture.runAsync(() -> sendNotificationToUsers(users, title, body));
    }
}
