package com.bilkom.service;

import com.bilkom.dto.ClubRegistrationRequestDTO;
import com.bilkom.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Service for sending various types of emails in the application.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${bilkom.admin.email}")
    private String adminEmail;
    
    @Value("${bilkom.base-url}")
    private String baseUrl;
    
    private final TemplateEngine templateEngine;
    
    /**
     * Constructor initializes the Thymeleaf template engine for HTML emails.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public EmailService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("email_templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        this.templateEngine = new SpringTemplateEngine();
        ((SpringTemplateEngine) this.templateEngine).setTemplateResolver(templateResolver);
    }

    /**
     * Sends a verification email to a user for account verification.
     * 
     * @param to The recipient's email address
     * @param verificationUrl The URL for verifying the account
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendVerificationEmail(String to, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your Bilkom account");
        message.setText("Welcome to Bilkom!\n\n"
                        + "Please click the link below to verify your email address:\n" 
                        + verificationUrl + "\n\n" 
                        + "If you did not request this, please ignore this email.");
        mailSender.send(message);
    }
    
    /**
     * Sends a password reset email to a user.
     * 
     * @param to The recipient's email address
     * @param resetUrl The URL for resetting the password
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your Bilkom password");
        message.setText("Hello,\n\n" 
                + "We received a request to reset your password. Please click the link below to set a new password:\n"
                + resetUrl + "\n\n" 
                + "If you did not request a password reset, please ignore this email or contact support if you have concerns.");
        
        mailSender.send(message);
    }
    
    /**
     * Sends a club registration verification email to the admin.
     * 
     * @param registrationRequest The club registration request details
     * @param executiveUser The user requesting to be the club executive
     * @param clubId The ID of the newly created club pending approval
     * @return The verification token generated for approval/rejection
     * @throws MessagingException If there's an error sending the email
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public String sendClubRegistrationVerificationEmail(ClubRegistrationRequestDTO registrationRequest, User executiveUser,Long clubId) throws MessagingException {
        
        // Generate a secure token for the approval/rejection process
        String verificationToken = UUID.randomUUID().toString();
        
        // Generate approval and rejection URLs
        String approveUrl = baseUrl + "/api/admin/clubs/approve?id=" + clubId + "&token=" + verificationToken;
        String rejectUrl = baseUrl + "/api/admin/clubs/reject?id=" + clubId + "&token=" + verificationToken;
        
        // Create the email context with all template variables
        Context context = new Context();
        context.setVariable("clubName", registrationRequest.getClubName());
        context.setVariable("clubDescription", registrationRequest.getClubDescription());
        context.setVariable("executiveName", executiveUser.getFirstName() + " " + executiveUser.getLastName());
        context.setVariable("executiveEmail", executiveUser.getEmail());
        context.setVariable("executivePosition", registrationRequest.getExecutivePosition());
        context.setVariable("verificationDocumentUrl", registrationRequest.getVerificationDocumentUrl());
        context.setVariable("additionalInfo", registrationRequest.getAdditionalInfo() != null ? registrationRequest.getAdditionalInfo() : 
                                "No additional information provided");
        context.setVariable("approveUrl", approveUrl);
        context.setVariable("rejectUrl", rejectUrl);
        
        // Process the template
        String emailContent = templateEngine.process("bilkom_backend\\bilkom_main\\src\\main\\resources\\email_templates\\club-registration-verification.html", context);
        
        // Create and send the email
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setTo(adminEmail);
        helper.setSubject("New Club Registration: " + registrationRequest.getClubName());
        helper.setText(emailContent, true); // true indicates HTML content
        
        mailSender.send(mimeMessage);
        
        return verificationToken;
    }
    
    /**
     * Sends a notification email to the user about their club registration status.
     * 
     * @param to The recipient's email address
     * @param clubName The name of the club
     * @param approved Whether the club was approved or rejected
     * @param reason The reason for rejection (if applicable)
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendClubRegistrationResultEmail(String to, String clubName, boolean approved, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        
        if (approved) {
            message.setSubject("Club Registration Approved: " + clubName);
            message.setText("Hello,\n\n"
                    + "Congratulations! Your club registration for \"" + clubName + "\" has been approved.\n\n"
                    + "You have been assigned as the club head and can now manage your club through the Bilkom platform.\n\n"
                    + "Thank you for enriching our campus community!\n\n"
                    + "The Bilkom Team");
        } 
        else {
            message.setSubject("Club Registration Rejected: " + clubName);
            message.setText("Hello,\n\n"
                    + "We regret to inform you that your club registration for \"" + clubName + "\" has been rejected.\n\n"
                    + "Reason: " + (reason != null ? reason : "No specific reason provided.") + "\n\n"
                    + "If you believe this is a mistake or would like to submit an improved registration, "
                    + "please contact the Bilkom administrators.\n\n"
                    + "The Bilkom Team");
        }
        
        mailSender.send(message);
    }

    /**
     * Sends a simple email with a subject and text content.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param text The email body text
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
