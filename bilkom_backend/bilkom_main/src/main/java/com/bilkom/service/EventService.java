package com.bilkom.service;

import com.bilkom.dto.EventDto;
import com.bilkom.entity.*;
import com.bilkom.exception.BadRequestException;
import com.bilkom.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;
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
}