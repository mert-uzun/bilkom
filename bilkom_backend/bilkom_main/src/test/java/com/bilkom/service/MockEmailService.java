package com.bilkom.service;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of EmailService for testing purposes.
 * Does not actually send emails but records information about attempted sends.
 */
@Service
@Profile("integration-test")
@Primary
public class MockEmailService extends EmailService {
    
    private List<EmailRecord> sentEmails = new ArrayList<>();
    
    // Record class to store information about sent emails
    public static class EmailRecord {
        private String to;
        private String subject;
        private String body;
        private String token;
        
        public EmailRecord(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
        
        public EmailRecord(String to, String subject, String body, String token) {
            this.to = to;
            this.subject = subject;
            this.body = body;
            this.token = token;
        }
        
        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getBody() { return body; }
        public String getToken() { return token; }
    }
    
    @Override
    public void sendVerificationEmail(String email, String token) {
        sentEmails.add(new EmailRecord(
            email, 
            "Verify Your Bilkom Account", 
            "This is a mock verification email.", 
            token
        ));
        System.out.println("[MOCK] Verification email sent to: " + email);
        System.out.println("[MOCK] Verification token: " + token);
    }
    
    @Override
    public void sendPasswordResetEmail(String email, String token) {
        sentEmails.add(new EmailRecord(
            email, 
            "Reset Your Bilkom Password", 
            "This is a mock password reset email.", 
            token
        ));
        System.out.println("[MOCK] Password reset email sent to: " + email);
        System.out.println("[MOCK] Reset token: " + token);
    }
    
    @Override
    public void sendClubRegistrationResultEmail(String email, String clubName, boolean approved, String reason) {
        String subject = approved ? "Club Registration Approved" : "Club Registration Denied";
        String body = approved ? 
            "Your club registration has been approved." : 
            "Your club registration has been denied. Reason: " + reason;
            
        sentEmails.add(new EmailRecord(email, subject, body));
        System.out.println("[MOCK] Club registration result email sent to: " + email);
        System.out.println("[MOCK] Club name: " + clubName);
        System.out.println("[MOCK] Approved: " + approved);
    }
    
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        sentEmails.add(new EmailRecord(to, subject, text));
        System.out.println("[MOCK] Simple email sent to: " + to);
        System.out.println("[MOCK] Subject: " + subject);
    }
    
    // Method to get all sent emails (for verification in tests)
    public List<EmailRecord> getSentEmails() {
        return sentEmails;
    }
    
    // Method to clear sent emails between tests
    public void clearSentEmails() {
        sentEmails.clear();
    }
} 