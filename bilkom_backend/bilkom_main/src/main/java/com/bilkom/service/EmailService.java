package com.bilkom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your Bilkom account");
        message.setText("Welcome to Bilkom!\n\n" +
                "Please click the link below to verify your email address:\n" +
                verificationUrl + "\n\n" +
                "If you did not request this, please ignore this email.");
        
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your Bilkom password");
        message.setText("Hello,\n\n" +
                "We received a request to reset your password. Please click the link below to set a new password:\n" +
                resetUrl + "\n\n" +
                "If you did not request a password reset, please ignore this email or contact support if you have concerns.");
        
        mailSender.send(message);
    }
}
