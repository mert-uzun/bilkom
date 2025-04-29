package com.bilkom.repository;

import com.bilkom.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EventRepository is an interface that extends JpaRepository for performing CRUD operations on Event entities.
 * It provides methods to interact with the database for Event-related data.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
public interface EventRepository extends JpaRepository<Event, Long> {}