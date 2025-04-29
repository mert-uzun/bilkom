package com.bilkom.service;

import com.bilkom.dto.EventDto;
import com.bilkom.entity.*;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new BadRequestException("User not found"));
    
        Event event = new Event();
        event.setEventName(dto.getName()); 
        event.setEventDescription(dto.getDescription());
        event.setEventLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentParticipantsNumber(0);
        event.setCreator(creator);
        event.setIsClubEvent(dto.getClubId() != null);
        event.setActive(true);
    
        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new BadRequestException("Club not found"));
            event.setClub(club);
        }
    
        for (String tagName : dto.getTags()) {
            Tag tag = new Tag();
            tag.setTagName(tagName);
            tag.setEvent(event);
            event.getTags().add(tag);
        }        
    
        return eventRepository.save(event);
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
    public void withdrawFromEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
        EventParticipantPK eventParticipantPK = new EventParticipantPK(eventId, user.getUserId());
        EventParticipant participant = participantRepository.findById(eventParticipantPK)
            .orElseThrow(() -> new BadRequestException("User is not a participant of the event"));
    
        participantRepository.delete(participant);
    
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));
    
        event.setCurrentParticipantsNumber(event.getCurrentParticipantsNumber() - 1);
        eventRepository.save(event);
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
}