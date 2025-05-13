package com.bilkom;

import com.bilkom.dto.AuthResponse;
import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.UserRepository;
import com.bilkom.security.JwtUtils;
import com.bilkom.service.AuthService;
import com.bilkom.service.EmailService;
import com.bilkom.service.TokenBlacklistService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for JWT token generation, validation, and blacklisting functionality.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class TokenExpirationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;

    private final String TEST_PASSWORD = "TestPassword123!";
    private String testEmail;

    @BeforeEach
    public void setUp() {
        // Mock email sending
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        // Create unique test email
        testEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@bilkent.edu.tr";
    }

    @Test
    public void testTokenGeneration() {
        // Register and verify user first
        User user = registerAndVerifyUser();
        
        // Verify user has proper role
        assertEquals(UserRole.USER, user.getRole());
        
        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(TEST_PASSWORD);
        
        // Login to get token
        AuthResponse response = authService.login(loginRequest);
        
        // Verify token was generated
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        
        // Verify token contains user details
        String token = response.getToken();
        String username = jwtUtils.getUsernameFromToken(token);
        assertEquals(testEmail, username);
    }
    
    @Test
    public void testTokenValidation() {
        // Register and verify user first
        User user = registerAndVerifyUser();
        
        // Ensure user is active
        assertTrue(user.isActive(), "User should be active for token validation");
        
        // Load user details and generate token directly
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);
        String token = jwtUtils.generateToken(userDetails);
        
        // Validate token
        boolean isValid = jwtUtils.validateToken(token);
        assertTrue(isValid);
    }
    
    @Test
    public void testTokenBlacklisting() {
        // Register and verify user first
        User user = registerAndVerifyUser();
        
        // Check user's ID is available
        assertNotNull(user.getUserId(), "User ID should be set");
        
        // Load user details and generate token directly
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);
        String token = jwtUtils.generateToken(userDetails);
        
        // Blacklist token
        jwtUtils.blacklistToken(token);
        
        // Token should now be blacklisted
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);
        assertTrue(isBlacklisted);
    }
    
    @Test
    public void testLoginWithInvalidCredentials() {
        // Register and verify user first
        User user = registerAndVerifyUser();
        
        // Confirm this user has USER role
        assertEquals(UserRole.USER, user.getRole(), "User should have USER role");
        
        // Create login request with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword("WrongPassword123!");
        
        // Just assert that login fails with bad credentials
        Exception exception = assertThrows(Exception.class, () -> authService.login(loginRequest));
        
        // Verify the error message contains appropriate text
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(errorMessage.contains("invalid") || errorMessage.contains("bad") || 
                   errorMessage.contains("wrong") || errorMessage.contains("credentials") ||
                   errorMessage.contains("password"),
                "Exception message should indicate invalid credentials: " + errorMessage);
    }
    
    @Test
    public void testLoginWithUnverifiedUser() {
        // Register user without verification
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        
        // Try to login without verification
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(TEST_PASSWORD);
        
        // This should throw an exception indicating the account is not verified
        Exception exception = assertThrows(Exception.class, () -> {authService.login(loginRequest);});
        
        // Verify the error message contains appropriate text
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(errorMessage.contains("invalid") || errorMessage.contains("verified") || errorMessage.contains("email") || errorMessage.contains("password"),"Exception message should indicate account is not verified or invalid credentials: " + errorMessage);
    }
    
    private RegistrationRequest createRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(testEmail);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBilkentId("2023" + (int)(Math.random() * 100000));
        request.setPhoneNumber("+90555" + (int)(Math.random() * 1000000));
        request.setBloodType("A+");
        return request;
    }
    
    private User registerAndVerifyUser() {
        // Register user
        RegistrationRequest request = createRegistrationRequest();
        authService.register(request);
        
        // Get user and set as verified
        User user = userRepository.findByEmail(testEmail).orElseThrow();
        user.setVerified(true);
        return userRepository.save(user);
    }
} 