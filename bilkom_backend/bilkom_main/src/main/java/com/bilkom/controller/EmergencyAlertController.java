package com.bilkom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bilkom.entity.EmergencyAlert;
import com.bilkom.service.EmergencyAlertService;

import java.util.List;

/**
 * EmergencyAlertController is responsible for handling HTTP requests related to emergency alerts.
 * It provides an endpoint for fetching all emergency alerts.
 *
 * @author: Elif Bozkurt
 * @version 1.0
 */
@RestController
@RequestMapping(path = "/api/v1/emergency-alerts")
public class EmergencyAlertController {
    private final EmergencyAlertService emergencyAlertService;

    @Autowired
    public EmergencyAlertController(EmergencyAlertService emergencyAlertService) {
        this.emergencyAlertService = emergencyAlertService;
    }

    /**
     * Fetches all emergency alerts.
     * 
     * @return List of EmergencyAlert objects
     * 
     * @author Elif Bozkurt
     * @version 1.0
     */
    @GetMapping
    public List<EmergencyAlert> getEmergencyAlerts() {
        return emergencyAlertService.fetchEmergencyAlerts();
    }    
}
