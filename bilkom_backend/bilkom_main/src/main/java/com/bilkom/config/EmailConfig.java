package com.bilkom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for email functionality.
 * Sets up JavaMailSender with SMTP settings for sending emails.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Configuration
public class EmailConfig {
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;
    
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailAuth;
    
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailStartTls;
    
    /**
     * Creates and configures JavaMailSender bean for sending emails.
     * 
     * @return Configured JavaMailSender instance
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Set mail server properties
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        // Set additional properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailAuth);
        props.put("mail.smtp.starttls.enable", mailStartTls);
        props.put("mail.debug", "true"); // Enable for now for debugging, will be disabled in production
        
        return mailSender;
    }
}
