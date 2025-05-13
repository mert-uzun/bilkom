package com.bilkom;

import com.bilkom.entity.Club;
import com.bilkom.entity.Event;
import com.bilkom.entity.User;
import com.bilkom.enums.ClubRegistrationStatus;
import com.bilkom.repository.ClubRepository;
import com.bilkom.repository.EventRepository;
import com.bilkom.repository.UserRepository;
import com.bilkom.service.EventSchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying that the event scheduler properly marks past events as inactive.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class EventSchedulerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EventSchedulerService eventSchedulerService;

    private User createUniqueUser() {
        String email = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@bilkent.edu.tr";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("testPassword123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBilkentId(UUID.randomUUID().toString().substring(0, 11));
        user.setPhoneNumber("555" + (int)(Math.random()*10000000));
        user.setBloodType("A+");
        user.setActive(true);
        user.setVerified(true);
        return userRepository.save(user);
    }

    private Club createUniqueClub(User user) {
        String clubName = "Test Club " + UUID.randomUUID().toString().substring(0, 8);
        Club club = new Club();
        club.setClubName(clubName);
        club.setClubDescription("Test Club Description");
        club.setClubHead(user);
        club.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        club.setActive(true);
        club.setStatus(ClubRegistrationStatus.PENDING);
        return clubRepository.save(club);
    }

    /**
     * Creates a test event with the specified date
     */
    private Event createEvent(User creator, Club club, LocalDate eventDate, boolean isActive) {
        String eventName = "Test Event " + UUID.randomUUID().toString().substring(0, 8);
        Event event = new Event();
        event.setEventName(eventName);
        event.setEventDescription("Test Event Description");
        event.setEventLocation("Test Location");
        event.setEventDate(Date.valueOf(eventDate));
        event.setMaxParticipants(10);
        event.setClub(club);
        event.setCreator(creator);
        event.setCurrentParticipantsNumber(0);
        event.setIsClubEvent(true);
        event.setActive(isActive);
        return eventRepository.save(event);
    }

    @Test
    public void testAutomaticEventMarking() {
        // Create test data
        User user = createUniqueUser();
        Club club = createUniqueClub(user);
        
        // Create events with different dates
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Create active past events that should be marked as inactive
        Event pastEvent1 = createEvent(user, club, yesterday, true);
        Event pastEvent2 = createEvent(user, club, twoDaysAgo, true);
        
        // Create already inactive past event (should remain inactive)
        Event alreadyInactivePastEvent = createEvent(user, club, yesterday, false);
        
        // Create future event (should remain active)
        Event futureEvent = createEvent(user, club, tomorrow, true);
        
        // Run the scheduler
        eventSchedulerService.markPastEvents();
        
        // Verify that past active events are now marked as inactive
        Event updatedPastEvent1 = eventRepository.findById(pastEvent1.getEventId()).orElseThrow();
        Event updatedPastEvent2 = eventRepository.findById(pastEvent2.getEventId()).orElseThrow();
        assertFalse(updatedPastEvent1.isActive(), "Past event 1 should be marked as inactive");
        assertFalse(updatedPastEvent2.isActive(), "Past event 2 should be marked as inactive");
        
        // Verify that already inactive past event remains inactive
        Event updatedInactivePastEvent = eventRepository.findById(alreadyInactivePastEvent.getEventId()).orElseThrow();
        assertFalse(updatedInactivePastEvent.isActive(), "Already inactive past event should remain inactive");
        
        // Verify that future event remains active
        Event updatedFutureEvent = eventRepository.findById(futureEvent.getEventId()).orElseThrow();
        assertTrue(updatedFutureEvent.isActive(), "Future event should remain active");
    }
} 