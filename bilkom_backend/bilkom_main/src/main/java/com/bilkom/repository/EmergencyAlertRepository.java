package com.bilkom.repository;

import com.bilkom.entity.EmergencyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EmergencyAlert entities.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
    
    /**
     * Finds all active emergency alerts.
     * 
     * @return List of active emergency alerts
     */
    List<EmergencyAlert> findByIsActiveTrue();
    
} 