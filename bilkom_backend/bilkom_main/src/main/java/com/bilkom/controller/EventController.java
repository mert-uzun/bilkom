package com.bilkom.controller;

import com.bilkom.dto.EventDto;
import com.bilkom.entity.Event;
import com.bilkom.entity.User;
import com.bilkom.enums.UserRole;
import com.bilkom.exception.BadRequestException;
import com.bilkom.service.EventService;
import com.bilkom.service.UserService;
import com.bilkom.service.ClubSecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * EventController is responsible for handling HTTP requests related to events.
 * It provides endpoints for creating, joining, withdrawing from, and listing events.
 * 
 * @author Elif Bozkurt
 */
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubSecurityService clubSecurityService; // Do not remove! This is used at runtime.

    /**
     * Creates a new event.
     * The event details are provided in the request body.
     * @param dto The event details
     * @param principal The authenticated user
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
     * Joins an event.
     * @param eventId The ID of the event
     * @param principal The authenticated user
     * @return ResponseEntity indicating success or failure.
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
     * Gets a specific event by its ID.
     * @param eventId The ID of the event
     * @return ResponseEntity containing the event details.
     * 
     * @author Elif Bozkurt
     * -- ADDED AFTER COMPARING WITH ANDROID --
     */
    @GetMapping("/{eventId}") 
    public ResponseEntity<Event> getEvent(@PathVariable Long eventId) {
        Event event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    /**
     * Updates an event.
     * The event details are provided in the request body.
     * @param eventId The ID of the event
     * @param dto The updated event details
     * @return ResponseEntity containing the updated event.
     * 
     * @author Elif Bozkurt
     * -- ADDED AFTER COMPARING WITH ANDROID --
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventDto dto) {
        Event updated = eventService.updateEvent(eventId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an event.
     * @param eventId The ID of the event
     * @return ResponseEntity indicating success or failure.
     * 
     * @author Elif Bozkurt
     * -- ADDED AFTER COMPARING WITH ANDROID --
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> reportPastEvent(@PathVariable Long eventId, @RequestBody Map<String, String> payload, Principal principal) {
        String reason = payload.get("reason");
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

    /**
     * Gets all events for a specific club.
     * 
     * @param clubId The ID of the club
     * @return ResponseEntity containing the list of all events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/clubs/{clubId}/events")
    @PreAuthorize("hasRole('ADMIN') or @clubSecurityService.isUserClubExecutiveOrHead(authentication.principal.userId, #clubId)")
    public ResponseEntity<List<Event>> getClubEvents(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getClubEvents(clubId));
    }
    
    /**
     * Gets current events for a specific club.
     * 
     * @param clubId The ID of the club
     * @return ResponseEntity containing the list of current events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/clubs/{clubId}/events/current")
    @PreAuthorize("hasRole('ADMIN') or @clubSecurityService.isUserClubExecutiveOrHead(authentication.principal.userId, #clubId)")
    public ResponseEntity<List<Event>> getCurrentClubEvents(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getCurrentClubEvents(clubId));
    }
    
    /**
     * Gets past events for a specific club.
     * 
     * @param clubId The ID of the club
     * @return ResponseEntity containing the list of past events for the club
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/clubs/{clubId}/events/past")
    @PreAuthorize("hasRole('ADMIN') or @clubSecurityService.isUserClubExecutiveOrHead(authentication.principal.userId, #clubId)")
    public ResponseEntity<List<Event>> getPastClubEvents(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getPastClubEvents(clubId));
    }
    
    /**
     * Gets all events for clubs where the current user is a club executive or head.
     * 
     * @return ResponseEntity containing a map of club IDs to event lists
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/my-club-events")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE')")
    public ResponseEntity<Map<Long, List<Event>>> getMyClubEvents() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(eventService.getAllEventsForClubExecutive(currentUser.getUserId()));
    }
    
    /**
     * Gets current events for clubs where the current user is a club executive or head.
     * 
     * @return ResponseEntity containing a map of club IDs to current event lists
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/my-club-events/current")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE')")
    public ResponseEntity<Map<Long, List<Event>>> getMyCurrentClubEvents() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(eventService.getCurrentEventsForClubExecutive(currentUser.getUserId()));
    }
    
    /**
     * Gets past events for clubs where the current user is a club executive or head.
     * 
     * @return ResponseEntity containing a map of club IDs to past event lists
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    @GetMapping("/my-club-events/past")
    @PreAuthorize("hasAnyRole('CLUB_HEAD', 'CLUB_EXECUTIVE')")
    public ResponseEntity<Map<Long, List<Event>>> getMyPastClubEvents() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(eventService.getPastEventsForClubExecutive(currentUser.getUserId()));
    }
}