package com.bilkom.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

/**
 * NotificationService is responsible for sending notifications using Firebase Cloud Messaging (FCM).
 * It provides a method to send FCM messages with a specified token, title, and body.
 * 
 * @author Elif Bozkurt
 * @version 2.0
 */
@Service
public class NotificationService {
    private final String SERVICE_ACCOUNT_PATH = "C:\\Users\\2007e\\Desktop\\Project\\bilkom\\bilkom_backend\\bilkom_main"; 

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
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new FileInputStream(SERVICE_ACCOUNT_PATH))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
            googleCredentials.refreshIfExpired();
            String accessToken = googleCredentials.getAccessToken().getTokenValue();

            URL url = new URL("https://fcm.googleapis.com/v1/projects/bilkom-11cc3/messages:send");
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
            System.out.println("FCM Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
