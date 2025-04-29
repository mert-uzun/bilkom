package com.bilkom.controller;

import com.bilkom.dto.EventDto;
import com.bilkom.entity.*;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * EventController is responsible for handling HTTP requests related to events.
 * It provides endpoints for creating, joining, withdrawing from, and listing events.
 * 
 * @author Elif Bozkurt
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    /**
     * * Creates a new event.
     * The event details are provided in the request body.
     * @param dto
     * @param principal
     * @return ResponseEntity containing the created event.
     * 
     * @author Elif Bozkurt
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDto dto, Principal principal) {
        Event created = eventService.createEvent(dto, principal.getName());
        return ResponseEntity.ok(created);        
    }

    /**
     * 
     * @param eventId
     * @param principal
     * @return ResponseEntity containing the event details.
     * 
     * @author Elif Bozkurt
     */
    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable Long eventId, Principal principal) {
        eventService.joinEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Withdraws the user from the event.
     * @param eventId
     * @param principal
     * @return ResponseEntity indicating success or failure.
     * 
     * @author Elif Bozkurt
     */
    @PostMapping("/{eventId}/withdraw")
    public ResponseEntity<Void> withdrawFromEvent(@PathVariable Long eventId, Principal principal) {
        eventService.withdrawFromEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Lists all events.
     * @return ResponseEntity containing the list of events.
     * 
     * @author Elif Bozkurt
     */
    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents() {
        List<Event> events = eventService.listAllEvents(); 
        return ResponseEntity.ok(events);
    }

    /**
     * Lists all active events.
     * @return ResponseEntity containing the list of active events.
     * 
     * @author Elif Bozkurt
     */
    @PostMapping("/filter")
    public ResponseEntity<List<Event>> filterEventsByTags(@RequestBody List<String> tagNames) {
        List<Event> events = eventService.filterEventsByTags(tagNames); 
        return ResponseEntity.ok(events);
    }

    /**
     * Lists all events created by the user.
     * @param principal
     * @return ResponseEntity containing the list of events created by the user.
     * 
     * @author Elif Bozkurt
     */
    @GetMapping("/created")
    public ResponseEntity<List<Event>> getEventsCreatedByUser(Principal principal) {
        List<Event> events = eventService.getEventsCreatedByUser(principal.getName());
        return ResponseEntity.ok(events);
    }

    /**
     * Lists all events the user has joined.
     * @param principal
     * @return ResponseEntity containing the list of events the user has joined.
     *
     * @author Elif Bozkurt
     */ 
    @GetMapping("/joined")
    public ResponseEntity<List<Event>> getEventsUserJoined(Principal principal) {
        List<Event> events = eventService.getEventsUserJoined(principal.getName());
        return ResponseEntity.ok(events);
    }

    /**
     * Lists all events created by the user.
     * @param principal
     * @return ResponseEntity containing the list of events created by the user.
     *
     * @author Elif Bozkurt
     */
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<User>> getParticipants(@PathVariable Long eventId, Principal principal) {
        List<User> users = eventService.getParticipantsForEvent(eventId, principal.getName());
        return ResponseEntity.ok(users);
    }

    /**
     * Lists all past events.
     * @return ResponseEntity containing the list of past events.
     *
     * @author Elif Bozkurt
     */
    @GetMapping("/past")
    public ResponseEntity<List<Event>> listPastEvents() {
        List<Event> pastEvents = eventService.listPastEvents();
        return ResponseEntity.ok(pastEvents);
    }

    /**
     * Lists all past events created by the user.
     * @param principal
     * @return ResponseEntity containing the list of past events created by the user.
     * 
     * @author Elif Bozkurt
     */
    @GetMapping("/created/past")
    public ResponseEntity<List<Event>> getPastEventsCreatedByUser(Principal principal) {
        List<Event> events = eventService.getPastEventsCreatedByUser(principal.getName());
        return ResponseEntity.ok(events);
    }

    /**
     * Lists all past events the user has joined.
     * @param principal
     * @return ResponseEntity containing the list of past events the user has joined.
     *
     * @author Elif Bozkurt
     */
    @GetMapping("/joined/past")
    public ResponseEntity<List<Event>> getPastEventsUserJoined(Principal principal) {
        List<Event> events = eventService.getPastEventsUserJoined(principal.getName());
        return ResponseEntity.ok(events);
    }

    /**
     * Marks the event as done.
     * @param eventId
     * @param principal
     * @return ResponseEntity indicating success or failure.
     *
     * @author Elif Bozkurt
     */
    @PostMapping("/{eventId}/done")
    public ResponseEntity<Void> markEventAsDone(@PathVariable Long eventId, Principal principal) {
        eventService.markEventAsDone(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Reports the event.
     * @param eventId
     * @param reason
     * @param principal
     * @return ResponseEntity indicating success or failure.
     *
     * @author Elif Bozkurt
     */
    @PostMapping("/{eventId}/report")
    public ResponseEntity<Void> reportPastEvent(@PathVariable Long eventId, @RequestBody String reason, Principal principal) {
        eventService.reportEvent(eventId, principal.getName(), reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new club event.
     * The event details are provided in the request body.
     * @param eventDto
     * @param principal
     * @return ResponseEntity containing the created event.
     *
     * @author Elif Bozkurt
     */
    @PostMapping("/create-club-event")
    public ResponseEntity<EventDto> createClubEvent(@RequestBody EventDto eventDto, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        
        if (user.getRole() != UserRole.CLUB_EXECUTIVE && user.getRole() != UserRole.CLUB_HEAD && user.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only Club Executives, Heads, or Admins can create club events.");
        }
        
        if (eventDto.getClubId() == null) {
            throw new BadRequestException("Club ID must be provided for a club event.");
        }
    
        eventDto.setIsClubEvent(true); 
    
        Event event = eventService.createEvent(eventDto, principal.getName());
        return ResponseEntity.ok(new EventDto(event));
    }
}