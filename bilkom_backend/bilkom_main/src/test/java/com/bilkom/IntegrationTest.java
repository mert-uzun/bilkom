package com.bilkom;

import com.bilkom.dto.AuthResponse;
import com.bilkom.dto.ClubDTO;
import com.bilkom.dto.EventDto;
import com.bilkom.dto.LoginRequest;
import com.bilkom.dto.RegistrationRequest;
import com.bilkom.entity.Club;
import com.bilkom.entity.Event;
import com.bilkom.entity.User;
import com.bilkom.entity.EventParticipant;
import com.bilkom.entity.EventParticipantPK;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.enums.UserRole;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.EventRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.repository.EventParticipantRepository;
import com.bilkom.service.AuthService;
import com.bilkom.service.ClubService;
import com.bilkom.service.EmailService;
import com.bilkom.service.EventService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Integration tests for end-to-end workflows using real external services.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;

    private final String TEST_PASSWORD = "TestPassword123!";

    @BeforeEach
    public void setUp() {
        // Mock email sending
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        
        // Mock all EmailService methods
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
        doNothing().when(emailService).sendClubRegistrationResultEmail(anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    public void testCompleteUserClubEventWorkflow() throws Exception {
        // Step 1: Register two users with test emails
        String email1 = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        String email2 = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        
        RegistrationRequest request1 = createRegistrationRequest(email1);
        RegistrationRequest request2 = createRegistrationRequest(email2);
        
        AuthResponse response1 = authService.register(request1);
        AuthResponse response2 = authService.register(request2);
        
        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        
        // Step 2: Verify emails
        User user1 = userRepository.findByEmail(email1).orElseThrow();
        User user2 = userRepository.findByEmail(email2).orElseThrow();
        
        String token1 = user1.getVerificationToken();
        String token2 = user2.getVerificationToken();
        
        boolean verified1 = authService.verifyEmail(token1);
        boolean verified2 = authService.verifyEmail(token2);
        
        assertTrue(verified1);
        assertTrue(verified2);
        
        // Refresh users
        user1 = userRepository.findByEmail(email1).orElseThrow();
        user2 = userRepository.findByEmail(email2).orElseThrow();
        
        // Step 3: Login
        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail(email1);
        loginRequest1.setPassword(TEST_PASSWORD);
        
        AuthResponse loginResponse1 = authService.login(loginRequest1);
        
        assertTrue(loginResponse1.isSuccess());
        assertNotNull(loginResponse1.getToken());
        
        // Step 4: Create a club with user1 as head
        String clubName = "Test Club " + UUID.randomUUID().toString().substring(0, 8);
        
        // Use the actual ClubService to create the club properly
        ClubDTO clubDTO = clubService.createClub(clubName, "A test club description", user1.getUserId());
        
        // Refresh the user to get updated role
        user1 = userRepository.findByEmail(email1).orElseThrow();
        clubRepository.findById(clubDTO.getClubId()).orElseThrow();
        
        assertNotNull(clubDTO);
        assertEquals(clubName, clubDTO.getClubName());
        assertEquals(user1.getUserId(), clubDTO.getClubHead().getUserId());
        
        // Step 5: Add user2 as a club member and executive
        clubService.addClubMember(clubDTO.getClubId(), user2.getUserId());
        clubService.addClubExecutive(clubDTO.getClubId(), user2.getUserId(), "Test Position");
        
        // Refresh user2 to see role change
        user2 = userRepository.findByEmail(email2).orElseThrow();
        assertEquals(UserRole.CLUB_EXECUTIVE, user2.getRole());
        
        // Step 6: Create an event for the club
        EventDto eventDto = new EventDto();
        eventDto.setName("Test Event " + UUID.randomUUID().toString().substring(0, 8));
        eventDto.setDescription("Test event description");
        eventDto.setLocation("Test Location");
        eventDto.setEventDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        eventDto.setMaxParticipants(20);
        eventDto.setClubId(clubDTO.getClubId());
        eventDto.setIsClubEvent(true);
        eventDto.setTags(Arrays.asList("test", "integration"));
        
        // Use the EventService to create the event
        Event savedEvent = eventService.createEvent(eventDto, email1);
        
        assertNotNull(savedEvent);
        assertEquals(eventDto.getName(), savedEvent.getEventName());
        assertEquals(clubDTO.getClubId(), savedEvent.getClub().getClubId());
        
        // Step 7: User2 joins the event
        // Use the event service to join
        eventService.joinEvent(savedEvent.getEventId(), email2);
        
        // Step 8: List events and verify participation
        List<Event> events = eventService.getEventsUserJoined(email2);
        
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().anyMatch(e -> e.getEventId().equals(savedEvent.getEventId())));
        
        // Step 9: User2 withdraws from the event using the service
        eventService.withdrawFromEvent(savedEvent.getEventId(), email2);
        
        // Verify withdrawal by directly checking if the EventParticipant entity exists for this user and event
        List<EventParticipant> participations = participantRepository.findByUser(user2);
        
        // Manually check if any participants with this event and user exist in the database
        EventParticipantPK pk = new EventParticipantPK(savedEvent.getEventId(), user2.getUserId());
        boolean participantExists = participantRepository.existsById(pk);
        
        assertFalse(participantExists, "Participant entry should be deleted from database");
        
        boolean isStillParticipant = participations.stream()
            .anyMatch(p -> p.getEvent().getEventId().equals(savedEvent.getEventId()));
        
        assertFalse(isStillParticipant, "User should no longer be a participant after withdrawal");
        
        // Also verify through the service
        events = eventService.getEventsUserJoined(email2);
        assertTrue(events.isEmpty() || events.stream().noneMatch(e -> e.getEventId().equals(savedEvent.getEventId())));
        
        // Step 10: Remove user2 as executive
        clubService.removeClubExecutive(clubDTO.getClubId(), user2.getUserId());
        
        // Refresh user2 to see role change
        user2 = userRepository.findByEmail(email2).orElseThrow();
        assertEquals(UserRole.USER, user2.getRole());
        
        // Step 11: Deactivate the club
        ClubDTO deactivatedClub = clubService.deactivateClub(clubDTO.getClubId());
        
        assertFalse(deactivatedClub.isActive());
    }

    @Test
    public void testClubStatusAndRepositoryOperations() {
        // Create a test user
        String email = "bilkom.test" + UUID.randomUUID().toString().substring(0, 6) + "@bilkent.edu.tr";
        RegistrationRequest request = createRegistrationRequest(email);
        authService.register(request);
        
        // Verify email
        User user = userRepository.findByEmail(email).orElseThrow();
        authService.verifyEmail(user.getVerificationToken());
        
        // Refresh user data
        user = userRepository.findByEmail(email).orElseThrow();
        
        // Create a club directly using repository
        Club club = new Club();
        club.setClubName("Repository Test Club " + UUID.randomUUID().toString().substring(0, 8));
        club.setClubDescription("A club created directly with repository");
        club.setClubHead(user);
        club.setStatus(ClubRegistrationStatus.PENDING); // Using ClubRegistrationStatus enum
        club.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        club.setActive(true);
        
        // Save using clubRepository
        Club savedClub = clubRepository.save(club);
        assertNotNull(savedClub.getClubId());
        assertEquals(ClubRegistrationStatus.PENDING, savedClub.getStatus());
        
        // Create an event directly using repository
        Event event = new Event();
        event.setEventName("Repository Test Event " + UUID.randomUUID().toString().substring(0, 8));
        event.setEventDescription("An event created directly with repository");
        event.setEventLocation("Test Location");
        event.setEventDate(new Date(System.currentTimeMillis() + 86400000));
        event.setMaxParticipants(50);
        event.setCurrentParticipantsNumber(0);
        event.setClub(savedClub);
        event.setCreator(user);
        event.setActive(true);
        event.setIsClubEvent(true);
        
        // Save using eventRepository
        Event savedEvent = eventRepository.save(event);
        assertNotNull(savedEvent.getEventId());
        
        // Verify club status change using repository
        savedClub.setStatus(ClubRegistrationStatus.APPROVED);
        Club updatedClub = clubRepository.save(savedClub);
        assertEquals(ClubRegistrationStatus.APPROVED, updatedClub.getStatus());
        
        // Search for club by status
        List<Club> approvedClubs = clubRepository.findByStatus(ClubRegistrationStatus.APPROVED);
        Long clubId = updatedClub.getClubId(); // Store ID in a final variable for lambda
        assertTrue(approvedClubs.stream().anyMatch(c -> c.getClubId().equals(clubId)));
        
        // Find events using the repository but with a more generic approach
        List<Event> allEvents = eventRepository.findAll();
        List<Event> clubEvents = allEvents.stream()
            .filter(e -> e.getClub() != null && e.getClub().getClubId().equals(clubId))
            .toList();
            
        assertFalse(clubEvents.isEmpty());
    }

    private RegistrationRequest createRegistrationRequest(String email) {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(email);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBilkentId("2023" + (int)(Math.random() * 100000));
        request.setPhoneNumber("+90555" + (int)(Math.random() * 10000000));
        request.setBloodType("A+");
        return request;
    }
} 