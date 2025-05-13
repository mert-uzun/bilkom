package com.bilkom;

import com.bilkom.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

/**
 * Integration tests for EmailService that verify actual email delivery.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Tag("IntegrationTest")
public class EmailServiceIntegrationTest {
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Tests sending a real verification email.
     */
    @Test
    public void testRealVerificationEmail() throws MessagingException {
        // Use your controlled test email here
        String testEmail = "mrtzuns@gmail.com";
        String verificationToken = UUID.randomUUID().toString();
        
        // Send a real verification email
        emailService.sendVerificationEmail(testEmail, verificationToken);
        
        // Log for manual verification
        System.out.println("Verification email sent to: " + testEmail);
        System.out.println("Verification token: " + verificationToken);
        System.out.println("Check your inbox to verify email was delivered correctly");
    }
    
    /**
     * Tests sending a real simple email.
     */
    @Test
    public void testRealSimpleEmail() throws MessagingException {
        // Use your controlled test email here
        String testEmail = "mrtzuns@gmail.com";
        
        // Send a real test email
        emailService.sendSimpleEmail(
            testEmail,
            "Test Email from Bilkom Integration Tests",
            "This is a test email to verify real sending works. Generated at: " + 
                java.time.LocalDateTime.now()
        );
        
        // Log for manual verification
        System.out.println("Simple test email sent to: " + testEmail);
        System.out.println("Check your inbox to verify email was delivered correctly");
    }
    
    /**
     * Tests sending a real password reset email.
     */
    @Test
    public void testRealPasswordResetEmail() throws MessagingException {
        // Use your controlled test email here
        String testEmail = "mrtzuns@gmail.com";
        String resetToken = UUID.randomUUID().toString();
        
        // Send a real password reset email
        emailService.sendPasswordResetEmail(testEmail, resetToken);
        
        // Log for manual verification
        System.out.println("Password reset email sent to: " + testEmail);
        System.out.println("Reset token: " + resetToken);
        System.out.println("Check your inbox to verify email was delivered correctly");
    }
    
    /**
     * Tests sending a real club registration approval email.
     */
    @Test
    public void testRealClubRegistrationApproval() throws MessagingException {
        // Use your controlled test email here
        String testEmail = "mrtzuns@gmail.com";
        String clubName = "Test Integration Club";
        
        // Send a real club approval email
        emailService.sendClubRegistrationResultEmail(testEmail, clubName, true, null);
        
        // Log for manual verification
        System.out.println("Club approval email sent to: " + testEmail);
        System.out.println("Check your inbox to verify email was delivered correctly");
    }
} 