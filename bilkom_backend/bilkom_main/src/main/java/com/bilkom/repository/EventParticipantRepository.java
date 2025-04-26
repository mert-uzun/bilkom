package com.bilkom.repository;

import com.bilkom.entity.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantPK> {
    boolean existsById(EventParticipantPK pk);
    List<EventParticipant> findByUser(User user);
}