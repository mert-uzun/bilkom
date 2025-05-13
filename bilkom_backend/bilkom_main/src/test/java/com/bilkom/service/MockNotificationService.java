package com.bilkom.service;

import com.bilkom.entity.User;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationService for testing purposes.
 * Does not actually send notifications via Firebase but records information about attempted sends.
 */
@Service
@Profile("test")
@Primary
public class MockNotificationService extends NotificationService {
    
    private List<NotificationRecord> sentNotifications = new ArrayList<>();
    
    // Record class to store information about sent notifications
    public static class NotificationRecord {
        private String token;
        private String title;
        private String body;
        private User user;
        
        public NotificationRecord(String token, String title, String body) {
            this.token = token;
            this.title = title;
            this.body = body;
        }
        
        public NotificationRecord(User user, String title, String body) {
            this.user = user;
            this.title = title;
            this.body = body;
            if (user != null) {
                this.token = user.getFcmToken();
            }
        }
        
        public String getToken() { return token; }
        public String getTitle() { return title; }
        public String getBody() { return body; }
        public User getUser() { return user; }
    }
    
    @Override
    public void sendFcm(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            System.out.println("[MOCK] Warning: Attempted to send FCM notification with null or empty token");
            return;
        }
        
        sentNotifications.add(new NotificationRecord(token, title, body));
        System.out.println("[MOCK] FCM notification sent to token: " + token);
        System.out.println("[MOCK] Title: " + title);
        System.out.println("[MOCK] Body: " + body);
    }
    
    @Override
    public void sendNotificationToUser(User user, String title, String body) {
        if (user == null) {
            System.out.println("[MOCK] Warning: Attempted to send notification to null user");
            return;
        }
        
        sentNotifications.add(new NotificationRecord(user, title, body));
        System.out.println("[MOCK] Notification sent to user: " + user.getEmail());
        System.out.println("[MOCK] Title: " + title);
        System.out.println("[MOCK] Body: " + body);
    }
    
    @Override
    public void sendNotificationToUsers(List<User> users, String title, String body) {
        if (users == null || users.isEmpty()) {
            System.out.println("[MOCK] Warning: Attempted to send notification to empty user list");
            return;
        }
        
        for (User user : users) {
            sendNotificationToUser(user, title, body);
        }
        
        System.out.println("[MOCK] Bulk notification sent to " + users.size() + " users");
    }
    
    // Method to get all sent notifications (for verification in tests)
    public List<NotificationRecord> getSentNotifications() {
        return sentNotifications;
    }
    
    // Method to clear sent notifications between tests
    public void clearSentNotifications() {
        sentNotifications.clear();
    }
} 