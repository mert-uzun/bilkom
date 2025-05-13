package com.bilkom.service;

import com.bilkom.entity.Event;
import com.bilkom.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service responsible for scheduling event-related tasks.
 * This includes automatically marking events as past when their date has passed.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Service
public class EventSchedulerService {
    
    private static final Logger logger = Logger.getLogger(EventSchedulerService.class.getName());
    
    @Autowired
    private EventRepository eventRepository;
    
    /**
     * Scheduled task that runs daily at midnight to identify and mark events as past.
     * Events are considered past when:
     * 1. Their event date is before the current date
     * 2. They are still marked as active
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void markPastEvents() {
        logger.info("Running scheduled task to mark past events");
        
        // Find all active events with a date before today
        Date today = new Date(System.currentTimeMillis());
        List<Event> pastEvents = eventRepository.findByEventDateBeforeAndIsActiveTrue(today);
        
        if (pastEvents.isEmpty()) {
            logger.info("No past events found that need to be marked");
            return;
        }
        
        logger.info("Found " + pastEvents.size() + " past events to mark");
        
        // Mark each event as inactive (past)
        for (Event event : pastEvents) {
            event.setActive(false);
            logger.info("Marked event as past: " + event.getEventId() + " - " + event.getEventName());
        }
        
        // Save all updated events
        eventRepository.saveAll(pastEvents);
        logger.info("Completed marking past events");
    }
} 