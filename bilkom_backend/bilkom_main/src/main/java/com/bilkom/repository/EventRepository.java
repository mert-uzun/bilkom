package com.bilkom.repository;

import com.bilkom.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}