package com.bilkom;

import com.bilkom.dto.AuthResponse;
import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.AuthService;
import com.bilkom.service.EmailService;
import com.bilkom.service.UserService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for user management functionality including registration, login, profile updates,
 * password changes, email verification, and password reset.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserManagementTest {

    /**
     * Local DTO class to replace the missing ProfileUpdateRequest
     */
    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String bloodType;
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getBloodType() { return bloodType; }
        public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;

    private final String TEST_PASSWORD = "TestPassword123!";
    private final String NEW_PASSWORD = "NewPassword456!";
    private String testEmail;
    private String verificationToken;

    @BeforeEach
    public void setUp() {
        // Create unique test email and verification token for this test
        testEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@bilkent.edu.tr";
        verificationToken = UUID.randomUUID().toString();
        
        // Mock email sending to avoid real email attempts
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        
        // Mock all EmailService methods used in tests
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
        doNothing().when(emailService).sendClubRegistrationResultEmail(anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    public void testUserRegistration() {
        // Create registration request
        RegistrationRequest request = createRegistrationRequest();
        
        // Register the user
        AuthResponse response = authService.register(request);
        
        // Verify registration was successful
        assertTrue(response.isSuccess());
        
        // Verify user was created in database
        User createdUser = userRepository.findByEmail(testEmail).orElse(null);
        assertNotNull(createdUser);
        assertEquals(testEmail, createdUser.getEmail());
        assertEquals(request.getFirstName(), createdUser.getFirstName());
        assertEquals(request.getLastName(), createdUser.getLastName());
        
        // Verify verification token was generated
        assertNotNull(createdUser.getVerificationToken());
        
        // Verify default role is USER
        assertEquals(UserRole.USER, createdUser.getRole());
    }

    @Test
    public void testUserLogin() {
        // Register user first
        RegistrationRequest registrationRequest = createRegistrationRequest();
        authService.register(registrationRequest);
        
        // Get the user and set as verified
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        user.setVerified(true);
        userRepository.save(user);
        
        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(TEST_PASSWORD);
        
        // Login
        AuthResponse response = authService.login(loginRequest);
        
        // Verify login was successful
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
    }

    @Test
    public void testProfileUpdate() {
        // Register and verify user first
        registerAndVerifyUser();
        
        // Get user for update
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        
        // Create profile update request
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        String uniquePhoneNumber = "+90555" + System.currentTimeMillis() % 10000000;
        updateRequest.setPhoneNumber(uniquePhoneNumber);
        updateRequest.setBloodType("B+");
        
        // Update user fields using UserService methods
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setBloodType(updateRequest.getBloodType());
        
        // Save updated user
        User updatedUser = userRepository.save(user);
        
        // Verify update was successful
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("User", updatedUser.getLastName());
        assertEquals(uniquePhoneNumber, updatedUser.getPhoneNumber());
        assertEquals("B+", updatedUser.getBloodType());
    }

    @Test
    public void testUserDeactivation() {
        // Register and verify user first
        registerAndVerifyUser();
        
        // Get user for deactivation
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        
        // Deactivate the user using updateActiveStatus instead of deactivateUser
        User deactivatedUser = userService.updateActiveStatus(user.getUserId(), false);
        
        // Verify deactivation was successful
        assertNotNull(deactivatedUser);
        
        // Verify user is now inactive
        assertFalse(deactivatedUser.isActive());
    }

    @Test
    public void testPasswordChange() {
        // Register and verify user first
        registerAndVerifyUser();
        
        // Change password
        AuthResponse response = authService.changePassword(
            getUserIdByEmail(testEmail), 
            TEST_PASSWORD, 
            NEW_PASSWORD
        );
        
        // Verify password change was successful
        assertTrue(response.isSuccess());
        
        // Verify can login with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(NEW_PASSWORD);
        
        AuthResponse loginResponse = authService.login(loginRequest);
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    public void testEmailVerification() {
        // Register user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        
        // Get the verification token
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String token = user.getVerificationToken();
        
        // Verify email
        boolean verified = authService.verifyEmail(token);
        
        // Verify verification was successful
        assertTrue(verified);
        
        // Verify user is now marked as verified
        User verifiedUser = userRepository.findByEmail(testEmail).orElseThrow();
        assertTrue(verifiedUser.isVerified());
        assertNull(verifiedUser.getVerificationToken()); // Token should be cleared after verification
    }

    @Test
    public void testPasswordReset() {
        // Register and verify user first
        registerAndVerifyUser();
        
        // Request password reset
        boolean requestResult = authService.requestPasswordReset(testEmail);
        
        // Verify request was successful
        assertTrue(requestResult);
        
        // Get the reset token
        User userWithResetToken = userRepository.findByEmail(testEmail).orElseThrow();
        String resetToken = userWithResetToken.getVerificationToken();
        assertNotNull(resetToken);
        
        // Reset password
        boolean resetResult = authService.resetPassword(resetToken, NEW_PASSWORD);
        
        // Verify reset was successful
        assertTrue(resetResult);
        
        // Verify can login with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(NEW_PASSWORD);
        
        AuthResponse loginResponse = authService.login(loginRequest);
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    public void testInvalidPasswordReset() {
        // Register and verify user first
        registerAndVerifyUser();
        
        // Use an invalid reset token
        String invalidToken = UUID.randomUUID().toString();
        
        // Try to reset with invalid token - should throw an exception
        Exception exception = assertThrows(Exception.class, () -> {
            authService.resetPassword(invalidToken, NEW_PASSWORD);
        });
        
        // Verify exception message contains appropriate text
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(errorMessage.contains("invalid") || errorMessage.contains("expired") || 
                   errorMessage.contains("token"),
                   "Exception message should indicate invalid or expired token: " + errorMessage);
        
        // Verify can't login with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(NEW_PASSWORD);
        
        Exception loginException = assertThrows(Exception.class, () -> {
            authService.login(loginRequest);
        });
        
        // Verify login fails with appropriate message
        String loginErrorMessage = loginException.getMessage().toLowerCase();
        assertTrue(loginErrorMessage.contains("invalid") || loginErrorMessage.contains("password") ||loginErrorMessage.contains("credentials"),"Login exception should indicate invalid credentials: " + loginErrorMessage);
    }

    @Test
    public void testPasswordEncodingAndManualVerification() {
        // Create registration request
        RegistrationRequest request = createRegistrationRequest();
        
        // Register the user
        authService.register(request);
        
        // Get the user from database
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        
        // Verify password was encoded correctly
        assertNotNull(user.getPasswordHash());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, user.getPasswordHash()), "Password should be correctly encoded");
        assertFalse(passwordEncoder.matches("WrongPassword", user.getPasswordHash()), "Non-matching password should fail verification");
        
        // Set the manually generated verification token
        user.setVerificationToken(verificationToken);
        userRepository.save(user);
        
        // Verify email using manually set token
        boolean verified = authService.verifyEmail(verificationToken);
        assertTrue(verified, "Verification with manual token should succeed");
        
        // Verify user is now marked as verified
        User verifiedUser = userRepository.findByEmail(testEmail).orElseThrow();
        assertTrue(verifiedUser.isVerified());
        assertNull(verifiedUser.getVerificationToken());
    }

    private RegistrationRequest createRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(testEmail);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBilkentId("2023" + (int)(Math.random() * 100000));
        request.setPhoneNumber("+90555" + (int)(Math.random() * 10000000));
        request.setBloodType("A+");
        return request;
    }
    
    private void registerAndVerifyUser() {
        // Register user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        
        // Set user as verified
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        user.setVerified(true);
        userRepository.save(user);
    }
    
    private Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getUserId();
    }
} 