package com.bilkom;

import com.bilkom.dto.ClubDTO;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.AdminVerificationService;
import com.bilkom.service.ClubRegistrationService;
import com.bilkom.service.EmailService;
import com.bilkom.service.UserService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for admin verification functionality including club approval and rejection.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class AdminRoleTest {

    @Autowired
    private AdminVerificationService adminService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @MockBean
    private ClubRegistrationService clubRegistrationService;
    
    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;
    
    private final String TEST_TOKEN = "test-verification-token";
    
    @BeforeEach
    public void setUp() {
        // Mock club registration service to avoid token issues
        when(clubRegistrationService.getClubIdByToken(TEST_TOKEN)).thenReturn(null);
        
        // Mock email sending
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        
        // Mock EmailService methods
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
    }
    
    @Test
    public void testApproveClub() {
        // Create a user who will be club head
        User user = createUniqueUser();
        
        // Create a pending club
        Club club = createPendingClub(user);
        
        // Mock token verification
        when(clubRegistrationService.getClubIdByToken(TEST_TOKEN)).thenReturn(club.getClubId());
        
        // Approve club
        ClubDTO approvedClubDTO = adminService.approveClub(club.getClubId(), TEST_TOKEN);
        
        // Verify club was approved
        assertNotNull(approvedClubDTO);
        assertEquals(ClubRegistrationStatus.APPROVED, approvedClubDTO.getStatus());
        
        // Verify user role was updated to CLUB_HEAD
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals(UserRole.CLUB_HEAD, updatedUser.getRole());
        
        // Verify the club is active
        Club updatedClub = clubRepository.findById(club.getClubId()).orElseThrow();
        assertTrue(updatedClub.isActive(), "Approved club should be active");
        assertEquals(ClubRegistrationStatus.APPROVED, updatedClub.getStatus());
    }
    
    @Test
    public void testRejectClub() {
        // Create a user who will be club head
        User user = createUniqueUser();
        
        // Create a pending club
        Club club = createPendingClub(user);
        
        // Mock token verification
        when(clubRegistrationService.getClubIdByToken(TEST_TOKEN)).thenReturn(club.getClubId());
        
        // Reject club with reason
        String rejectionReason = "Club does not meet requirements";
        ClubDTO rejectedClubDTO = adminService.rejectClub(club.getClubId(), TEST_TOKEN, rejectionReason);
        
        // Verify club was rejected
        assertNotNull(rejectedClubDTO);
        assertEquals(ClubRegistrationStatus.REJECTED, rejectedClubDTO.getStatus());
        
        // Verify user role remains unchanged
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals(UserRole.USER, updatedUser.getRole());
        
        // Verify the club status in the database
        Club updatedClub = clubRepository.findById(club.getClubId()).orElseThrow();
        assertEquals(ClubRegistrationStatus.REJECTED, updatedClub.getStatus());
    }
    
    @Test
    public void testInvalidTokenHandling() {
        // Create a user who will be club head
        User user = createUniqueUser();
        
        // Create a pending club
        Club club = createPendingClub(user);
        
        // Mock token verification to return null (invalid token)
        when(clubRegistrationService.getClubIdByToken("invalid-token")).thenReturn(null);
        
        // Attempt to approve club with invalid token
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.approveClub(club.getClubId(), "invalid-token");
        });
        
        // Verify that an appropriate exception was thrown
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(errorMessage.contains("invalid") || errorMessage.contains("token") || 
                   errorMessage.contains("verification"), 
                   "Exception should indicate token verification failure: " + errorMessage);
        
        // Verify club status remains PENDING
        Club unchangedClub = clubRepository.findById(club.getClubId()).orElseThrow();
        assertEquals(ClubRegistrationStatus.PENDING, unchangedClub.getStatus());
    }
    
    @Test
    public void testGetPendingClubs() {
        // Create several users
        User user1 = createUniqueUser();
        User user2 = createUniqueUser();
        User user3 = createUniqueUser();
        
        // Create several pending clubs
        Club club1 = createPendingClub(user1);
        Club club2 = createPendingClub(user2);
        Club club3 = createPendingClub(user3);
        
        // Get pending clubs from the database
        List<Club> pendingClubs = clubRepository.findByStatus(ClubRegistrationStatus.PENDING);
        
        // Verify that our test clubs are in the list
        assertTrue(pendingClubs.size() >= 3, "Should have at least 3 pending clubs");
        assertTrue(pendingClubs.stream().anyMatch(club -> club.getClubId().equals(club1.getClubId())), 
                  "List should contain club1");
        assertTrue(pendingClubs.stream().anyMatch(club -> club.getClubId().equals(club2.getClubId())), 
                  "List should contain club2");
        assertTrue(pendingClubs.stream().anyMatch(club -> club.getClubId().equals(club3.getClubId())), 
                  "List should contain club3");
    }
    
    @Test
    public void testAdminUserVerificationWithEmail() {
        // Create a user
        User user = createUniqueUser();
        
        // Set user role to ADMIN
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        
        // Mock verification with any string input
        when(clubRegistrationService.getClubIdByToken(anyString())).thenReturn(null);
        
        // Verify the user role has been updated to ADMIN
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
        
        // Use userService to check if user exists by ID
        boolean userExists = userService.isUserExists(user.getUserId());
        assertTrue(userExists, "User should exist in the database");
        
        // Verify admin user can be found by email
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        assertTrue(foundUser.isPresent());
        assertEquals(UserRole.ADMIN, foundUser.get().getRole());
    }
    
    private User createUniqueUser() {
        String email = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@bilkent.edu.tr";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBilkentId("2023" + (int)(Math.random() * 100000));
        user.setPhoneNumber("+90555" + (int)(Math.random() * 10000000));
        user.setBloodType("A+");
        user.setPasswordHash("HashedPassword");
        user.setVerified(true);
        user.setActive(true);
        user.setRole(UserRole.USER);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
    
    private Club createPendingClub(User clubHead) {
        String clubName = "Test Club " + UUID.randomUUID().toString().substring(0, 8);
        Club club = new Club();
        club.setClubName(clubName);
        club.setClubDescription("Test Club Description");
        club.setClubHead(clubHead);
        club.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        club.setActive(true);
        club.setStatus(ClubRegistrationStatus.PENDING);
        return clubRepository.save(club);
    }
} 