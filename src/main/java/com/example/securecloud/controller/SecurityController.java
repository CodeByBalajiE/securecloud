package com.example.securecloud.controller;

import com.example.securecloud.model.AnomalyAlert;
import com.example.securecloud.service.SecurityMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    private SecurityMonitorService monitorService;

    // ✅ Get all anomaly alerts
    @GetMapping("/alerts")
    public ResponseEntity<List<AnomalyAlert>> getAllAlerts() {
        return ResponseEntity.ok(monitorService.getAllAlerts());
    }

    // ✅ Get alerts by user
    @GetMapping("/alerts/{userId}")
    public ResponseEntity<List<AnomalyAlert>> getAlertsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(monitorService.getAlertsByUser(userId));
    }
}
