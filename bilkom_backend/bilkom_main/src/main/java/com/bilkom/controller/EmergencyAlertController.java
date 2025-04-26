package com.bilkom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bilkom.entity.EmergencyAlert;
import com.bilkom.service.EmergencyAlertService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/emergency-alerts")
public class EmergencyAlertController {

    private final EmergencyAlertService emergencyAlertService;

    @Autowired
    public EmergencyAlertController(EmergencyAlertService emergencyAlertService) {
        this.emergencyAlertService = emergencyAlertService;
    }

    @GetMapping
    public List<EmergencyAlert> getEmergencyAlerts() {
        return emergencyAlertService.fetchEmergencyAlerts();
    }    
}
