package com.bilkom;

import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.ClubRegistrationRequestDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.ClubRegistrationService;
import com.bilkom.service.EmailService;
import com.bilkom.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Integration tests for club registration functionality including registration submission,
 * approval, and rejection using real services instead of mocks.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class ClubRegistrationServiceTest {

    @Autowired
    private ClubRegistrationService clubRegistrationService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private NotificationService notificationService;

    private User executiveUser;
    private Club pendingClub;
    private Club approvedClub;
    private Club rejectedClub;
    private ClubRegistrationRequestDTO registrationRequest;
    private String verificationToken;

    @BeforeEach
    public void setUp() {
        // Mock email sending
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        
        // Mock all EmailService methods
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
        doNothing().when(emailService).sendClubRegistrationResultEmail(anyString(), anyString(), anyBoolean(), anyString());
        
        // Mock NotificationService methods
        doNothing().when(notificationService).sendNotificationToUser(any(User.class), anyString(), anyString());
        doNothing().when(notificationService).sendNotificationToUsers(anyList(), anyString(), anyString());
        
        // Create test executive user
        executiveUser = createUser("executive_user_" + System.currentTimeMillis() + "@bilkent.edu.tr", UserRole.USER);
        
        // Create a pending club
        pendingClub = new Club();
        pendingClub.setClubName("Pending Club");
        pendingClub.setClubDescription("Test pending club description");
        pendingClub.setClubHead(executiveUser);
        pendingClub.setStatus(ClubRegistrationStatus.PENDING);
        pendingClub.setActive(true);
        pendingClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        pendingClub = clubRepository.save(pendingClub);
        
        // Create an approved club
        approvedClub = new Club();
        approvedClub.setClubName("Approved Club");
        approvedClub.setClubDescription("Test approved club description");
        approvedClub.setClubHead(executiveUser);
        approvedClub.setStatus(ClubRegistrationStatus.APPROVED);
        approvedClub.setActive(true);
        approvedClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        approvedClub = clubRepository.save(approvedClub);
        
        // Create a rejected club
        rejectedClub = new Club();
        rejectedClub.setClubName("Rejected Club");
        rejectedClub.setClubDescription("Test rejected club description");
        rejectedClub.setClubHead(executiveUser);
        rejectedClub.setStatus(ClubRegistrationStatus.REJECTED);
        rejectedClub.setActive(false);
        rejectedClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        rejectedClub = clubRepository.save(rejectedClub);
        
        // Create a registration request
        registrationRequest = new ClubRegistrationRequestDTO();
        registrationRequest.setClubName("New Test Club");
        registrationRequest.setClubDescription("Test club description");
        registrationRequest.setExecutiveUserId(executiveUser.getUserId());
        
        // Generate a test verification token
        verificationToken = "test_token_" + System.currentTimeMillis();
    }

    @AfterEach
    public void tearDown() {
        // Clean up created entities (though @Transactional should roll them back)
        try {
            clubRepository.deleteAll();
            userRepository.delete(executiveUser);
            
            // Clean up any token we might have created
            if (verificationToken != null) {
                clubRegistrationService.removeToken(verificationToken);
            }
        } catch (Exception e) {
            System.err.println("Error during test cleanup: " + e.getMessage());
        }
    }

    @Test
    public void testGetPendingRegistrations() {
        // Get pending registrations
        List<ClubDTO> result = clubRegistrationService.getPendingRegistrations();
        
        // Verify result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Make sure our pending club is in the list
        boolean foundPendingClub = false;
        for (ClubDTO club : result) {
            if (club.getClubId().equals(pendingClub.getClubId())) {
                foundPendingClub = true;
                assertEquals(pendingClub.getClubName(), club.getClubName());
                assertEquals(pendingClub.getClubDescription(), club.getClubDescription());
                assertEquals(pendingClub.getClubHead().getUserId(), club.getClubHead().getUserId());
                break;
            }
        }
        assertTrue(foundPendingClub, "The pending club should be in the list");
    }

    @Test
    public void testGetRejectedRegistrations() {
        // Get rejected registrations
        List<ClubDTO> result = clubRegistrationService.getRejectedRegistrations();
        
        // Verify result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Make sure our rejected club is in the list
        boolean foundRejectedClub = false;
        for (ClubDTO club : result) {
            if (club.getClubId().equals(rejectedClub.getClubId())) {
                foundRejectedClub = true;
                assertEquals(rejectedClub.getClubName(), club.getClubName());
                break;
            }
        }
        assertTrue(foundRejectedClub, "The rejected club should be in the list");
    }

    @Test
    public void testGetPendingRegistrationById() {
        // Get pending registration by ID
        ClubDTO result = clubRegistrationService.getPendingRegistrationById(pendingClub.getClubId());
        
        // Verify result
        assertNotNull(result);
        assertEquals(pendingClub.getClubId(), result.getClubId());
        assertEquals(pendingClub.getClubName(), result.getClubName());
    }

    @Test
    public void testGetPendingRegistrationById_NonPendingClub() {
        // Try to get a non-pending club as pending
        assertThrows(BadRequestException.class, () -> 
            clubRegistrationService.getPendingRegistrationById(rejectedClub.getClubId()));
    }

    @Test
    public void testRegisterClub() throws MessagingException {
        // Make sure to use a unique club name to avoid conflicts
        String uniqueClubName = "Test Club " + System.currentTimeMillis();
        registrationRequest.setClubName(uniqueClubName);
        
        // Register club
        ClubDTO result = clubRegistrationService.registerClub(registrationRequest);
        
        // Verify result
        assertNotNull(result);
        assertEquals(uniqueClubName, result.getClubName());
        assertEquals(registrationRequest.getClubDescription(), result.getClubDescription());
        assertEquals(executiveUser.getUserId(), result.getClubHead().getUserId());
        
        // Try to get the verification token, but since we don't control email
        // sending in integration tests, we can't verify this fully
    }

    @Test
    public void testRegisterClub_DuplicateName() {
        // Set the name to match the existing pending club
        registrationRequest.setClubName(pendingClub.getClubName());
        
        // Try to register club with duplicate name
        assertThrows(BadRequestException.class, () -> 
            clubRegistrationService.registerClub(registrationRequest));
    }

    @Test
    public void testIsClubNameAvailable() {
        // Check availability
        assertTrue(clubRegistrationService.isClubNameAvailable("Available Name " + System.currentTimeMillis()));
        assertFalse(clubRegistrationService.isClubNameAvailable(pendingClub.getClubName()));
    }

    @Test
    public void testTokenManagement() throws MessagingException {
        // First verify that we get null for a random token
        String randomToken = "random_token_" + System.currentTimeMillis();
        Long retrievedClubId = clubRegistrationService.getClubIdByToken(randomToken);
        assertNull(retrievedClubId);
        
        // The real token verification is harder to test in integration tests
        // because we don't have access to the tokens generated by the email service
        // In a real world scenario, we might have a test email service that captures tokens
    }

    private User createUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBilkentId("20" + System.currentTimeMillis() % 10000);
        user.setPasswordHash("hashedPassword");
        user.setPhoneNumber("+90555" + (int)(Math.random() * 10000000));
        user.setBloodType("A+");
        user.setRole(role);
        user.setActive(true);
        user.setVerified(true);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
} 