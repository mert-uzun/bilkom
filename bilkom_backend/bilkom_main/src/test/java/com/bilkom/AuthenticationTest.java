package com.bilkom;

import com.bilkom.dto.AuthResponse;
import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.entity.User;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.AuthService;
import com.bilkom.service.EmailService;
import com.bilkom.service.TokenBlacklistService;
import com.bilkom.security.JwtUtils;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for authentication, password reset, and token management using real services.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;

    private String testEmail;
    private final String TEST_PASSWORD = "TestPassword123!";
    private String newPassword = "NewPassword456!";

    @BeforeEach
    public void setUp() {
        // Mock email sending
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
        
        // Create a unique email for each test
        testEmail = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
    }

    @Test
    public void testUserRegistration() {
        RegistrationRequest request = createRegistrationRequest();
        AuthResponse response = authService.register(request);
        
        assertTrue(response.isSuccess());
        User user = userRepository.findByEmail(testEmail).orElse(null);
        assertNotNull(user);
        assertEquals(testEmail, user.getEmail());
        assertFalse(user.isVerified()); 
        assertNotNull(user.getVerificationToken()); 
    }

    @Test
    public void testEmailVerification() {
        // Register a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        
        // Get the verification token
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        
        // Verify the email
        boolean verified = authService.verifyEmail(verificationToken);
        
        assertTrue(verified);
        User verifiedUser = userRepository.findByEmail(testEmail).orElseThrow();
        assertTrue(verifiedUser.isVerified());
        assertNull(verifiedUser.getVerificationToken()); // Token should be cleared after verification
    }

    @Test
    public void testPasswordResetRequest() {
        // Register and verify a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        authService.verifyEmail(verificationToken);
        
        // Request password reset
        boolean resetRequested = authService.requestPasswordReset(testEmail);
        
        assertTrue(resetRequested);
        User userWithResetToken = userRepository.findByEmail(testEmail).orElseThrow();
        assertNotNull(userWithResetToken.getVerificationToken()); // Should have a new token for reset
    }

    @Test
    public void testPasswordReset() {
        // Register and verify a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        authService.verifyEmail(verificationToken);
        
        // Request password reset
        authService.requestPasswordReset(testEmail);
        User userWithResetToken = userRepository.findByEmail(testEmail).orElseThrow();
        String resetToken = userWithResetToken.getVerificationToken();
        
        // Reset password
        boolean resetResult = authService.resetPassword(resetToken, newPassword);
        
        assertTrue(resetResult);
        User userAfterReset = userRepository.findByEmail(testEmail).orElseThrow();
        assertNull(userAfterReset.getVerificationToken()); // Token should be cleared
        
        // Try to login with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(newPassword);
        
        AuthResponse loginResponse = authService.login(loginRequest);
        assertTrue(loginResponse.isSuccess());
        assertNotNull(loginResponse.getToken());
    }

    @Test
    public void testTokenBlacklisting() {
        // Register and verify a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        authService.verifyEmail(verificationToken);
        
        // Login to get a token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(TEST_PASSWORD);
        
        AuthResponse loginResponse = authService.login(loginRequest);
        String token = loginResponse.getToken();
        
        // Logout/blacklist the token
        authService.logout(user.getUserId(), "Bearer " + token);
        
        // Check if token is blacklisted
        assertTrue(tokenBlacklistService.isBlacklisted(token), "Token should be blacklisted");
    }

    @Test
    public void testInvalidPasswordResetToken() {
        // Test with an invalid reset token
        String invalidToken = UUID.randomUUID().toString();
        
        // This should throw an exception
        Exception exception = assertThrows(Exception.class, () -> {
            authService.resetPassword(invalidToken, newPassword);
        });
        
        assertTrue(exception.getMessage().contains("Invalid") || 
                  exception.getMessage().contains("token"));
    }

    @Test
    public void testChangePassword() {
        // Register and verify a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        authService.verifyEmail(verificationToken);
        
        // Change password
        AuthResponse response = authService.changePassword(user.getUserId(), TEST_PASSWORD, newPassword);
        
        assertTrue(response.isSuccess());
        
        // Try to login with new password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(newPassword);
        
        AuthResponse loginResponse = authService.login(loginRequest);
        assertTrue(loginResponse.isSuccess());
    }
    
    @Test
    public void testManualAuthenticationAndTokenGeneration() {
        // Register and verify a user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        String verificationToken = user.getVerificationToken();
        authService.verifyEmail(verificationToken);
        
        // Manually authenticate with AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(testEmail, TEST_PASSWORD)
        );
        
        // Verify authentication was successful
        assertTrue(authentication.isAuthenticated());
        assertEquals(testEmail, authentication.getName());
        
        // Generate JWT token using jwtUtils
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);
        String token = jwtUtils.generateToken(userDetails);
        
        // Verify token
        assertNotNull(token);
        assertEquals(testEmail, jwtUtils.getUsernameFromToken(token));
        assertTrue(jwtUtils.validateToken(token));
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
} 