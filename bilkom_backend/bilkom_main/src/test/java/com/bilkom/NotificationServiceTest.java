package com.bilkom;

import com.bilkom.entity.User;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for NotificationService using real Firebase Cloud Messaging.
 * Note: These tests require a valid FCM configuration and token to work.
 * 
 * @author Mert Uzun
 * @version 1.2
 */
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;

    // Use test FCM token from application properties
    @Value("${fcm.test.token:}")
    private String testFcmToken;
    
    private final String TEST_TITLE = "Test Notification Title";
    private final String TEST_BODY = "This is a test notification from the Bilkom integration tests.";
    
    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        // No setup needed as we're using the real NotificationService
        // The service will use the actual configuration from application-test.properties
        assertNotNull(testFcmToken, "Test FCM token must be configured in application-test.properties");
        assertFalse(testFcmToken.isEmpty(), "Test FCM token cannot be empty");
        
        // Create test users with FCM tokens
        String email1 = "notification.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        testUser1 = createTestUser(email1, testFcmToken);
        
        String email2 = "notification.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        testUser2 = createTestUser(email2, "invalid_token_for_testing");
        
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
    }

    @Test
    public void testSendFcmNotification() {
        // This will send a real FCM notification
        // We're only verifying that no exception is thrown
        assertDoesNotThrow(() -> 
            notificationService.sendFcm(testFcmToken, TEST_TITLE, TEST_BODY),
            "Should be able to send notification without exceptions"
        );
        
        // Note: We can't programmatically verify that the message was delivered
        // That would require manual verification on the test device
        System.out.println("FCM notification sent. Please check the test device for the notification.");
    }

    @Test
    public void testSendFcmWithInvalidToken() {
        // Test with invalid token - should not throw exception but fail gracefully
        assertDoesNotThrow(() -> 
            notificationService.sendFcm("invalid_token_123", TEST_TITLE, TEST_BODY),
            "Service should handle invalid tokens gracefully"
        );
    }

    @Test
    public void testSendFcmWithNullToken() {
        // Test with null token - should not throw exception
        assertDoesNotThrow(() -> 
            notificationService.sendFcm(null, TEST_TITLE, TEST_BODY),
            "Service should handle null token gracefully"
        );
    }

    @Test
    public void testSendFcmWithSpecialCharacters() {
        // Test with special characters in the message
        String specialCharTitle = "Special chars: !@#$%^&*()";
        String specialCharBody = "Message with 特殊字符 and emojis 😀🔥👍";
        
        assertDoesNotThrow(() -> 
            notificationService.sendFcm(testFcmToken, specialCharTitle, specialCharBody),
            "Service should handle special characters in messages"
        );
    }
    
    @Test
    public void testSendNotificationToUser() {
        // Test sending notification directly to a user entity
        assertDoesNotThrow(() -> 
            notificationService.sendNotificationToUser(testUser1, TEST_TITLE, TEST_BODY),
            "Should be able to send notification to a user with token"
        );
    }
    
    @Test
    public void testSendNotificationToUserWithInvalidToken() {
        // Test sending notification to a user with invalid token
        assertDoesNotThrow(() -> 
            notificationService.sendNotificationToUser(testUser2, TEST_TITLE, TEST_BODY),
            "Should handle sending notification to a user with invalid token"
        );
    }
    
    @Test
    public void testSendNotificationToUsers() {
        // Test sending notification to multiple users
        List<User> users = List.of(testUser1, testUser2);
        
        assertDoesNotThrow(() -> 
            notificationService.sendNotificationToUsers(users, TEST_TITLE, TEST_BODY),
            "Should handle sending notification to multiple users"
        );
    }
    
    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_CONCURRENT_TESTS", matches = "true")
    public void testConcurrentNotifications() throws InterruptedException, ExecutionException, TimeoutException {
        // Test sending multiple notifications concurrently
        final int NOTIFICATION_COUNT = 5;
        CompletableFuture<?>[] futures = new CompletableFuture<?>[NOTIFICATION_COUNT];
        
        for (int i = 0; i < NOTIFICATION_COUNT; i++) {
            final int index = i;
            futures[i] = CompletableFuture.runAsync(() -> 
                notificationService.sendFcm(testFcmToken, 
                    TEST_TITLE + " #" + index, 
                    TEST_BODY + " Concurrent test #" + index)
            );
        }
        
        // Wait for all notifications to be sent
        CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        
        // If we get here, all notifications were sent without exception
        assertTrue(true, "All concurrent notifications should be sent without errors");
    }
    
    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_LONG_TESTS", matches = "true")
    public void testHighVolumeNotifications() {
        // Test sending a high volume of notifications (depends on FCM rate limits)
        final int NOTIFICATION_COUNT = 20; // Keep reasonable to avoid FCM rate limits
        
        for (int i = 0; i < NOTIFICATION_COUNT; i++) {
            final int index = i;
            assertDoesNotThrow(() -> 
                notificationService.sendFcm(testFcmToken, 
                    TEST_TITLE + " #" + index, 
                    TEST_BODY + " High volume test #" + index),
                "Should handle high volume notification #" + index
            );
            
            // Add a small delay to avoid FCM rate limiting
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private User createTestUser(String email, String fcmToken) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Notification");
        user.setLastName("Test");
        user.setBilkentId("20" + UUID.randomUUID().toString().substring(0, 6));
        user.setPasswordHash("hashedPassword");
        user.setPhoneNumber("+90555" + UUID.randomUUID().toString().substring(0, 7));
        user.setBloodType("A+");
        user.setFcmToken(fcmToken);
        user.setActive(true);
        user.setVerified(true);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return user;
    }
} 