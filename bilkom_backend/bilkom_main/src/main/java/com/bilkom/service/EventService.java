package com.bilkom.service;

import com.bilkom.dto.EventDto;
import com.bilkom.dto.ClubDTO;
import com.bilkom.entity.*;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * EventService is responsible for handling business logic related to events.
 * It provides methods for creating, joining, withdrawing from, and listing events.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PastEventReportRepository pastEventReportRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubService clubService;

    /**
     * Creates a new event based on the provided EventDto and the creator's email.
     * 
     * @param dto EventDto containing event details
     * @param creatorEmail Email of the user creating the event
     * @return Created Event object
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    public Event createEvent(EventDto dto, String creatorEmail) {
        System.out.println("Creating event for user: " + creatorEmail);
        System.out.println("Incoming EventDto: " + dto);

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new BadRequestException("User not found"));
        System.out.println("Creator found: " + creator.getEmail());

        Event event = new Event();
        event.setEventName(dto.getName());
        event.setEventDescription(dto.getDescription());
        event.setEventLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentParticipantsNumber(0);
        event.setCreator(creator);

        boolean isClub = dto.isClubEvent();
        event.setIsClubEvent(isClub);
        event.setActive(true);
        System.out.println("isClubEvent: " + isClub);

        if (isClub) {
            if (dto.getClubId() == null) {
                System.out.println("Error: Club ID is required for club events.");
                throw new BadRequestException("Club ID is required for club events.");
            }
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new BadRequestException("Club not found"));
            event.setClub(club);
            System.out.println("Club set: " + club.getClubName());
        } else {
            event.setClub(null);
            System.out.println("Club is not set (public event)");
        }

        for (String tagName : dto.getTags()) {
            Tag tag = new Tag();
            tag.setTagName(tagName);
            tag.setEvent(event);
            event.getTags().add(tag);
            System.out.println("Tag added: " + tagName);
        }

        Event saved = eventRepository.save(event);
        System.out.println("Event saved with ID: " + saved.getEventId());
        return saved;
    }


    /**
     * Updates an existing event based on the provided EventDto and the event ID.
     * 
     * @param dto EventDto containing updated event details
     * @param eventId ID of the event to be updated
     * @return Updated Event object
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    public void joinEvent(Long eventId, String userEmail) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));
    
        if (event.getCurrentParticipantsNumber() >= event.getMaxParticipants()) {
            throw new BadRequestException("Event is full");
        }
    
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
        if (event.getParticipants().stream().anyMatch(p -> p.getUser().equals(user))) {
            throw new BadRequestException("User already joined the event");
        }
    
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setUser(user);
        participantRepository.save(eventParticipant); 
    
        event.setCurrentParticipantsNumber(event.getCurrentParticipantsNumber() + 1);
        eventRepository.save(event); 
    }
    
    /**
     * Withdraws a user from an event based on the provided event ID and user email.
     * 
     * @param eventId ID of the event to withdraw from
     * @param userEmail Email of the user withdrawing from the event
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    @Transactional
    public void withdrawFromEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));
    
        EventParticipantPK eventParticipantPK = EventParticipantPK.fromEntities(event, user);
        
        if (!participantRepository.existsById(eventParticipantPK)) {
            throw new BadRequestException("User is not a participant of the event");
        }
        
        // First remove the participant from the event's collection
        event.getParticipants().removeIf(participant -> 
            participant.getUser().getUserId().equals(user.getUserId())
        );
        
        // Update the count
        event.setCurrentParticipantsNumber(Math.max(0, event.getCurrentParticipantsNumber() - 1));
        
        // Save the event to persist the changes to the collection
        eventRepository.save(event);
        
        // Then delete from the repository directly
        participantRepository.deleteById(eventParticipantPK);
    }
    
    public List<Event> listAllEvents() {
        return eventRepository.findAll().stream()
            .filter(Event::isActive)
            .collect(Collectors.toList());
    }    

    public List<Event> filterEventsByTags(List<String> tagNames) {
        return eventRepository.findAll().stream()
            .filter(event -> event.getTags().stream()
                    .anyMatch(tag -> tagNames.contains(tag.getTagName())))
            .collect(Collectors.toList());
    }

    public List<Event> getEventsCreatedByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
        return eventRepository.findAll().stream()
            .filter(event -> event.getCreator().getUserId().equals(user.getUserId()))
            .collect(Collectors.toList());
    } 

    public List<Event> getEventsUserJoined(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
        List<EventParticipant> participations = participantRepository.findByUser(user);
    
        return participations.stream()
            .map(EventParticipant::getEvent)
            .filter(Event::isActive)
            .collect(Collectors.toList());
    }

    public List<User> getParticipantsForEvent(Long eventId, String requesterEmail) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));
    
        if (!event.getCreator().getEmail().equals(requesterEmail)) {
            throw new BadRequestException("You are not authorized to view this event's participants.");
        }
    
        return event.getParticipants().stream()
            .map(EventParticipant::getUser)
            .collect(Collectors.toList());
    }    

    public List<Event> listPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAll().stream()
            .filter(event -> !event.isActive() || event.getEventDate().toLocalDate().isBefore(today))
            .collect(Collectors.toList());
    }

    public List<Event> getPastEventsCreatedByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));

        LocalDate today = LocalDate.now();
        return eventRepository.findAll().stream()
            .filter(event -> event.getCreator().getUserId().equals(user.getUserId()))
            .filter(event -> !event.isActive() || event.getEventDate().toLocalDate().isBefore(today))
            .collect(Collectors.toList());
    }

    public List<Event> getPastEventsUserJoined(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));

        List<EventParticipant> participations = participantRepository.findByUser(user);

        LocalDate today = LocalDate.now();
        return participations.stream()
            .map(EventParticipant::getEvent)
            .filter(event -> !event.isActive() || event.getEventDate().toLocalDate().isBefore(today))
            .collect(Collectors.toList());
    }

    /**
     * Marks an event as done based on the provided event ID and creator's email.
     * 
     * @param eventId ID of the event to be marked as done
     * @param creatorEmail Email of the user who created the event
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    public void markEventAsDone(Long eventId, String creatorEmail) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));

        if (!event.getCreator().getEmail().equals(creatorEmail)) {
            throw new BadRequestException("You are not authorized to mark this event as done.");
        }

        event.setActive(false);
        eventRepository.save(event);
    }

    /**
     * Reports an event based on the provided event ID, reporter's email, and reason.
     * 
     * @param eventId ID of the event to be reported
     * @param reporterEmail Email of the user reporting the event
     * @param reason Reason for reporting the event
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    public void reportEvent(Long eventId, String reporterEmail, String reason) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));

        User reporter = userRepository.findByEmail(reporterEmail)
            .orElseThrow(() -> new BadRequestException("Reporter not found"));

        PastEventReport report = new PastEventReport();
        report.setEvent(event);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setReportedAt(LocalDateTime.now());

        pastEventReportRepository.save(report);
    }

    /**
     * Retrieves all events associated with a specific club.
     * 
     * @param clubId The ID of the club
     * @return List of all events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<Event> getClubEvents(Long clubId) {
        // Check if club exists
        clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found"));
            
        return eventRepository.findByClubClubId(clubId);
    }
    
    /**
     * Retrieves current (active and upcoming) events associated with a specific club.
     * 
     * @param clubId The ID of the club
     * @return List of current events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<Event> getCurrentClubEvents(Long clubId) {
        // Check if club exists
        clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found"));
            
        Date today = new Date(System.currentTimeMillis());
        return eventRepository.findByClubClubIdAndEventDateGreaterThanEqual(clubId, today).stream().filter(Event::isActive).collect(Collectors.toList());
    }
    
    /**
     * Retrieves past events associated with a specific club.
     * 
     * @param clubId The ID of the club
     * @return List of past events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<Event> getPastClubEvents(Long clubId) {
        // Check if club exists
        clubRepository.findById(clubId).orElseThrow(() -> new BadRequestException("Club not found"));
            
        Date today = new Date(System.currentTimeMillis());
        List<Event> pastByDate = eventRepository.findByClubClubIdAndEventDateBefore(clubId, today);
        List<Event> inactiveEvents = eventRepository.findByClubClubIdAndIsActiveTrue(clubId).stream().filter(event -> !event.isActive()).collect(Collectors.toList());
            
        // Combine past by date and inactive events, removing duplicates
        List<Event> allPastEvents = new ArrayList<>(pastByDate);
        for (Event event : inactiveEvents) {
            if (!allPastEvents.contains(event)) {
                allPastEvents.add(event);
            }
        }
        
        return allPastEvents;
    }
    
    /**
     * Retrieves all events across clubs where the user is a club executive.
     * 
     * @param userId The ID of the user
     * @return Map containing event lists categorized by club ID
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Map<Long, List<Event>> getAllEventsForClubExecutive(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
            
        // Verify user is a club executive or head
        if (user.getRole() != UserRole.CLUB_EXECUTIVE && user.getRole() != UserRole.CLUB_HEAD) {
            throw new BadRequestException("User is not a club executive or head");
        }
        
        Map<Long, List<Event>> result = new HashMap<>();
        
        // Get clubs where user is an executive
        List<ClubDTO> executiveClubs = clubService.getClubsByExecutiveId(userId);
        
        for (ClubDTO club : executiveClubs) {
            List<Event> clubEvents = getClubEvents(club.getClubId());
            result.put(club.getClubId(), clubEvents);
        }
        
        // If user is a club head, add those clubs too
        if (user.getRole() == UserRole.CLUB_HEAD) {
            List<ClubDTO> headClubs = clubService.getClubsByHeadId(userId);
            
            for (ClubDTO club : headClubs) {
                if (!result.containsKey(club.getClubId())) {
                    List<Event> clubEvents = getClubEvents(club.getClubId());
                    result.put(club.getClubId(), clubEvents);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Retrieves current events across clubs where the user is a club executive.
     * 
     * @param userId The ID of the user
     * @return Map containing current event lists categorized by club ID
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Map<Long, List<Event>> getCurrentEventsForClubExecutive(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
            
        // Verify user is a club executive or head
        if (user.getRole() != UserRole.CLUB_EXECUTIVE && user.getRole() != UserRole.CLUB_HEAD) {
            throw new BadRequestException("User is not a club executive or head");
        }
        
        Map<Long, List<Event>> result = new HashMap<>();
        
        // Get clubs where user is an executive
        List<ClubDTO> executiveClubs = clubService.getClubsByExecutiveId(userId);
        
        for (ClubDTO club : executiveClubs) {
            List<Event> clubEvents = getCurrentClubEvents(club.getClubId());
            result.put(club.getClubId(), clubEvents);
        }
        
        // If user is a club head, add those clubs too
        if (user.getRole() == UserRole.CLUB_HEAD) {
            List<ClubDTO> headClubs = clubService.getClubsByHeadId(userId);
            
            for (ClubDTO club : headClubs) {
                if (!result.containsKey(club.getClubId())) {
                    List<Event> clubEvents = getCurrentClubEvents(club.getClubId());
                    result.put(club.getClubId(), clubEvents);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Retrieves past events across clubs where the user is a club executive.
     * 
     * @param userId The ID of the user
     * @return Map containing past event lists categorized by club ID
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public Map<Long, List<Event>> getPastEventsForClubExecutive(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
            
        // Verify user is a club executive or head
        if (user.getRole() != UserRole.CLUB_EXECUTIVE && user.getRole() != UserRole.CLUB_HEAD) {
            throw new BadRequestException("User is not a club executive or head");
        }
        
        Map<Long, List<Event>> result = new HashMap<>();
        
        // Get clubs where user is an executive
        List<ClubDTO> executiveClubs = clubService.getClubsByExecutiveId(userId);
        
        for (ClubDTO club : executiveClubs) {
            List<Event> clubEvents = getPastClubEvents(club.getClubId());
            result.put(club.getClubId(), clubEvents);
        }
        
        // If user is a club head, add those clubs too
        if (user.getRole() == UserRole.CLUB_HEAD) {
            List<ClubDTO> headClubs = clubService.getClubsByHeadId(userId);
            
            for (ClubDTO club : headClubs) {
                if (!result.containsKey(club.getClubId())) { // Avoid duplicates
                    List<Event> clubEvents = getPastClubEvents(club.getClubId());
                    result.put(club.getClubId(), clubEvents);
                }
            }
        }
        
        return result;
    }
}