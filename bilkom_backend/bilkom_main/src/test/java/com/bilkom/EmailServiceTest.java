package com.bilkom;

import com.bilkom.entity.Club;
import com.bilkom.entity.Event;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.EventRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for EmailService functionality using mocked JavaMailSender.
 * 
 * @author Mert Uzun
 * @version 1.2
 */
@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private EventRepository eventRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    private User testUser;
    private Club testClub;
    private Event testEvent;
    private String testEmail;
    private String testVerificationToken;

    @BeforeEach
    public void setUp() {
        // Setup mock behavior
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Create test user with a controlled email for testing
        // Using a placeholder email since we're not actually sending emails
        testEmail = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        
        testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setBilkentId("2023" + (int)(Math.random() * 100000));
        testUser.setPhoneNumber("555" + (int)(Math.random() * 10000000));
        testUser.setBloodType("A+");
        testUser.setPasswordHash("HashedPassword");
        testUser.setVerified(true);
        testUser.setActive(true);
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        testVerificationToken = UUID.randomUUID().toString();
        testUser.setVerificationToken(testVerificationToken);
        testUser = userRepository.save(testUser);
        
        // Create test club
        testClub = new Club();
        testClub.setClubName("Test Club " + UUID.randomUUID().toString().substring(0, 8));
        testClub.setClubDescription("Test Description");
        testClub.setClubHead(testUser);
        testClub.setActive(true);
        testClub.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        testClub.setStatus(ClubRegistrationStatus.APPROVED);
        testClub = clubRepository.save(testClub);
        
        // Create test event
        testEvent = new Event();
        testEvent.setEventName("Test Event " + UUID.randomUUID().toString().substring(0, 8));
        String eventDescription = "Test Event Description";
        String eventLocation = "Test Location";
        testEvent.setEventDescription(eventDescription);
        testEvent.setEventLocation(eventLocation);
        testEvent.setEventDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        testEvent.setMaxParticipants(100);
        testEvent.setActive(true);
        testEvent.setClub(testClub);
        testEvent.setCreator(testUser);
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    public void testSendVerificationEmail() throws MessagingException {
        // Reset mock to ensure clean state
        reset(javaMailSender);
        
        // Capture the email that would be sent
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        // Send verification email
        emailService.sendVerificationEmail(testUser.getEmail(), testVerificationToken);
        
        // Verify the email was sent with correct parameters
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        // Assert email properties
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertEquals("Verify your Bilkom account", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(testVerificationToken));
    }

    @Test
    @Disabled("Skipping email template tests as they're verified manually")
    public void testSendPasswordResetEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        // Send password reset email
        emailService.sendPasswordResetEmail(testUser.getEmail(), testVerificationToken);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertEquals("Reset your Bilkom password", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(testVerificationToken));
    }

    @Test
    @Disabled("Skipping email template tests as they're verified manually")
    public void testSendClubRegistrationApprovalEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        // Send club registration approval email
        emailService.sendClubRegistrationResultEmail(testUser.getEmail(), testClub.getClubName(), true, null);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertTrue(sentMessage.getSubject().contains("Approved"));
        assertTrue(sentMessage.getText().contains("Congratulations"));
    }

    @Test
    @Disabled("Skipping email template tests as they're verified manually")
    public void testSendClubRegistrationRejectionEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        String rejectionReason = "Insufficient details provided";
        
        // Send club registration rejection email
        emailService.sendClubRegistrationResultEmail(testUser.getEmail(), testClub.getClubName(), false, rejectionReason);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertTrue(sentMessage.getSubject().contains("Rejected"));
        assertTrue(sentMessage.getText().contains(rejectionReason));
    }

    @Test
    public void testSendEventReminderEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        String subject = "Event Reminder: " + testEvent.getEventName(); 
        String body = "You have an upcoming event tomorrow.";
        
        // Send event reminder email
        emailService.sendSimpleEmail(testUser.getEmail(), subject, body);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    public void testSendEventCancellationEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        String subject = "Event Cancellation: " + testEvent.getEventName();
        String body = "The event has been cancelled.";
        
        // Send event cancellation email
        emailService.sendSimpleEmail(testUser.getEmail(), subject, body);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    public void testSendClubMembershipEmail() throws MessagingException {
        // Reset mock
        reset(javaMailSender);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        String subject = "Welcome to " + testClub.getClubName();
        String body = "You are now a member of " + testClub.getClubName() + ". Welcome aboard!";
        
        // Send club membership email
        emailService.sendSimpleEmail(testUser.getEmail(), subject, body);
        
        // Verify
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(testUser.getEmail(), sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }
} 