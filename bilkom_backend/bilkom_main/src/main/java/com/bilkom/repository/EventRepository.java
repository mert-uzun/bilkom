package com.bilkom.repository;

import com.bilkom.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

/**
 * EventRepository is an interface that extends JpaRepository for performing CRUD operations on Event entities.
 * It provides methods to interact with the database for Event-related data.
 * 
 * @author Elif Bozkurt , Mert Uzun
 * @version 1.1
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Returns all the events of a club specified by clubId
     * @param clubId the id of the club
     * @return a list of events
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<Event> findByClubClubId(Long clubId);
    
    /**
     * Returns all the active events of a club specified by clubId
     * @param clubId the id of the club
     * @return a list of events
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<Event> findByClubClubIdAndIsActiveTrue(Long clubId);
    
    /**
     * Returns all the events of a club specified by clubId where the event date is before the given date
     * @param clubId the id of the club
     * @param date the date to compare with
     * @return a list of events
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<Event> findByClubClubIdAndEventDateBefore(Long clubId, Date date);
    
    /**
     * Returns all the events of a club specified by clubId where the event date is after or equal to the given date
     * @param clubId the id of the club
     * @param date the date to compare with
     * @return a list of events
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    List<Event> findByClubClubIdAndEventDateGreaterThanEqual(Long clubId, Date date);
}