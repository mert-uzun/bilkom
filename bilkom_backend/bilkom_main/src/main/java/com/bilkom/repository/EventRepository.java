package com.bilkom.repository;

import com.bilkom.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Date;
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByClubClubId(Long clubId);

    List<Event> findByClubClubIdAndEventDateAfter(Long clubId, LocalDateTime date);

    List<Event> findByClubClubIdAndEventDateBefore(Long clubId, LocalDateTime date);

    List<Event> findByEventDateBeforeAndIsActiveTrue(Date date);

    List<Event> findByClubClubIdAndEventDateGreaterThanEqual(Long clubId, Date date);

    List<Event>findByClubClubIdAndEventDateBefore(Long clubId, Date date);

    List<Event> findByClubClubIdAndIsActiveTrue(Long clubId);
}