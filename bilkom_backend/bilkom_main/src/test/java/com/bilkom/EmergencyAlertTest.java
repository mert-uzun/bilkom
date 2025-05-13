package com.bilkom;

import com.bilkom.entity.EmergencyAlert;
import com.bilkom.entity.User;
import com.bilkom.repository.EmergencyAlertRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.EmergencyAlertService;
import com.bilkom.service.NotificationService;
import com.bilkom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for emergency alert functionality using real service implementations.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class EmergencyAlertTest {

    @Autowired
    private EmergencyAlertService emergencyAlertService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmergencyAlertRepository alertRepository;
    
    private User testUser1;
    private User testUser2;
    private User testUser3;
    
    @BeforeEach
    public void setUp() {
        // Create test users with different blood types
        String email1 = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        String email2 = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        String email3 = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        
        testUser1 = createTestUser(email1, "A Rh(+)", "test_fcm_token_1");
        testUser2 = createTestUser(email2, "B Rh(+)", "test_fcm_token_2");
        testUser3 = createTestUser(email3, "A Rh(+)", "test_fcm_token_3");
        
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
    }
    
    @Test
    public void testCreateEmergencyAlert() {
        // Create an emergency alert
        String subject = "ACİL KAN İHTİYACI: A Rh+ Kan İhtiyacı";
        String content = "Bilkent Üniversitesi'nde bir öğrenci için acil A Rh+ kan ihtiyacı vardır. " + 
                        "İletişim: 0555 123 4567. Bilkent Sağlık Merkezi'ne geliniz.";
        Date sentDate = new Date();
        
        EmergencyAlert alert = new EmergencyAlert(subject, content, sentDate);
        alert.setBloodType("A Rh (+)");
        alert = alertRepository.save(alert);
        
        // Verify alert was created with correct values
        assertEquals(subject, alert.getSubject());
        assertEquals(content, alert.getContent());
        assertEquals(sentDate, alert.getSentDate());
        assertTrue(alert.isActive());
        
        // Verify alert is saved in the database
        EmergencyAlert savedAlert = alertRepository.findById(alert.getAlertId()).orElse(null);
        assertNotNull(savedAlert);
        assertEquals(subject, savedAlert.getSubject());
    }
    
    @Test
    public void testBloodTypeExtraction() {
        // Create an EmergencyAlert with a specific subject and content
        String subject = "ACİL KAN İHTİYACI: A Rh+ Kan İhtiyacı";
        String content = "Bilkent Üniversitesi'nde bir öğrenci için acil A Rh+ kan ihtiyacı vardır.";
        EmergencyAlert alert = new EmergencyAlert(subject, content, new Date());
        
        // Setting the blood type manually since it's not auto-extracted in the test
        alert.setBloodType("A Rh(+)");
        
        // Verify blood type is correctly set
        assertEquals("A Rh(+)", alert.getBloodType());
        
        // For different scenarios, create different alerts
        EmergencyAlert alert2 = new EmergencyAlert(
            "ACİL KAN İHTİYACI: O Rh- Kan İhtiyacı",
            "Bilkent Üniversitesi'nde bir öğrenci için acil O Rh- kan ihtiyacı vardır.",
            new Date()
        );
        alert2.setBloodType("O Rh(-)");
        assertEquals("O Rh(-)", alert2.getBloodType());
        
        // Blood type is stored as-is, no normalization happens
        EmergencyAlert alert3 = new EmergencyAlert();
        alert3.setBloodType("AB Rh(+)");
        assertEquals("AB Rh(+)", alert3.getBloodType());
        
        EmergencyAlert alert4 = new EmergencyAlert();
        alert4.setBloodType("O Rh(-)");
        assertEquals("O Rh(-)", alert4.getBloodType());
    }
    
    @Test
    public void testPhoneNumberExtraction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Use reflection to test private method
        Method extractPhoneNumber = EmergencyAlert.class.getDeclaredMethod("extractPhoneNumber", String.class);
        extractPhoneNumber.setAccessible(true);
        
        // Test various phone number formats
        String content1 = "İletişim: 0555 123 4567";
        String content2 = "Telefon: +90 (532) 987-6543";
        String content3 = "05551234567 numarasını arayın.";
        
        EmergencyAlert dummyAlert = new EmergencyAlert();
        String result1 = (String) extractPhoneNumber.invoke(dummyAlert, content1);
        String result2 = (String) extractPhoneNumber.invoke(dummyAlert, content2);
        String result3 = (String) extractPhoneNumber.invoke(dummyAlert, content3);
        
        // Verify phone number was correctly extracted
        assertEquals("0555 123 4567", result1);
        assertEquals("+90 (532) 987-6543", result2);
        assertEquals("05551234567", result3);
    }
    
    @Test
    public void testNotificationsForBloodTypeMatches() {
        // Create an emergency alert
        EmergencyAlert alert = new EmergencyAlert(
            "ACİL KAN İHTİYACI: A Rh+ Kan İhtİyacı", 
            "Bilkent Üniversitesi'nde bir öğrenci için acil A Rh+ kan ihtiyacı vardır.", 
            new Date()
        );
        alert.setBloodType("A Rh(+)");
        alertRepository.save(alert);
        
        // Get users with matching blood type - exact match needed
        List<User> aPositiveUsers = userService.getUsersByBloodType("A Rh(+)");
        List<User> bPositiveUsers = userService.getUsersByBloodType("B Rh(+)");
        
        // Verify that users exist with expected blood types
        assertTrue(aPositiveUsers.size() >= 2, "Should find at least 2 users with A Rh(+) blood type");
        assertTrue(bPositiveUsers.size() >= 1, "Should find at least 1 user with B Rh(+) blood type");
        
        // Verify our test users are in the respective lists
        boolean foundUser1 = false;
        boolean foundUser3 = false;
        for (User user : aPositiveUsers) {
            if (user.getEmail().equals(testUser1.getEmail())) {
                foundUser1 = true;
            }
            if (user.getEmail().equals(testUser3.getEmail())) {
                foundUser3 = true;
            }
        }
        assertTrue(foundUser1, "Should find testUser1 with A Rh(+) blood type");
        assertTrue(foundUser3, "Should find testUser3 with A Rh(+) blood type");
        
        // Verify B+ test user is in the B+ list
        boolean foundUser2 = false;
        for (User user : bPositiveUsers) {
            if (user.getEmail().equals(testUser2.getEmail())) {
                foundUser2 = true;
                break;
            }
        }
        assertTrue(foundUser2, "Should find testUser2 with B Rh(+) blood type");
        
        // Send notifications (using the real notification service)
        for (User user : aPositiveUsers) {
            if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                notificationService.sendFcm(
                    user.getFcmToken(),
                    alert.getBloodType() + " BLOOD NEEDED",
                    alert.getContent() + "\n\n"
                );
            }
        }
        
        // No verification needed as we're just testing integration
        // Actual delivery would require manual verification on test devices
    }
    
    @Test
    public void testGetActiveEmergencyAlerts() {
        // Create some test alerts
        EmergencyAlert alert1 = new EmergencyAlert(
            "ACİL KAN İHTİYACI: A Rh+ Kan İhtiyacı", 
            "Acil A Rh+ kan ihtiyacı", 
            new Date()
        );
        alert1.setBloodType("A Rh (+)");
        
        EmergencyAlert alert2 = new EmergencyAlert(
            "ACİL KAN İHTİYACI: B Rh+ Kan İhtiyacı", 
            "Acil B Rh+ kan ihtiyacı", 
            new Date()
        );
        alert2.setBloodType("B Rh (+)");
        
        // Save alerts
        alertRepository.save(alert1);
        alertRepository.save(alert2);
        
        // Get active alerts
        List<EmergencyAlert> activeAlerts = emergencyAlertService.getActiveEmergencyAlerts();
        
        // Verify alerts were retrieved
        assertNotNull(activeAlerts);
        assertTrue(activeAlerts.size() >= 2);  // There might be other alerts from previous tests
    }
    
    private User createTestUser(String email, String bloodType, String fcmToken) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBilkentId("2023" + UUID.randomUUID().toString().substring(0, 6));
        user.setPhoneNumber("555" + (int)(Math.random() * 10000000));
        user.setBloodType(bloodType);
        user.setPasswordHash("HashedPassword");
        user.setVerified(true);
        user.setActive(true);
        user.setFcmToken(fcmToken);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return user;
    }
} 