package com.bilkom.controller;

import com.bilkom.dto.EventDto;
import com.bilkom.entity.Event;
import com.bilkom.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDto dto, Principal principal) {
        Event created = eventService.createEvent(dto, principal.getName());
        return ResponseEntity.ok(created);        
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable Long eventId, Principal principal) {
        eventService.joinEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/withdraw")
    public ResponseEntity<Void> withdrawFromEvent(@PathVariable Long eventId, Principal principal) {
        eventService.withdrawFromEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents() {
        List<Event> events = eventService.listAllEvents(); 
        return ResponseEntity.ok(events);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Event>> filterEventsByTags(@RequestBody List<String> tagNames) {
        List<Event> events = eventService.filterEventsByTags(tagNames); 
        return ResponseEntity.ok(events);
    }

    @GetMapping("/created")
    public ResponseEntity<List<Event>> getEventsCreatedByUser(Principal principal) {
        List<Event> events = eventService.getEventsCreatedByUser(principal.getName());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/joined")
    public ResponseEntity<List<Event>> getEventsUserJoined(Principal principal) {
        List<Event> events = eventService.getEventsUserJoined(principal.getName());
        return ResponseEntity.ok(events);
    }
}
