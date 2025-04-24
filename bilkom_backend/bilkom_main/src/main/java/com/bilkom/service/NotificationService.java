package com.bilkom.service;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NotificationService {

    private final String FCM_SERVER_KEY = "YOUR_FCM_SERVER_KEY";

    public void sendFcm(String token, String title, String body) {
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            String payload = """
                {
                  "to": "%s",
                  "notification": {
                    "title": "%s",
                    "body": "%s"
                  }
                }
                """.formatted(token, title, body);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            conn.getResponseCode(); // trigger and check for errors
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
