package com.example.securecloud.service;

import com.example.securecloud.model.AnomalyAlert;
import com.example.securecloud.repository.AnomalyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityMonitorService {

    @Autowired
    private AnomalyRepository anomalyRepo;

    @Autowired
    private EmailService emailService;

    // ✅ Log and email admin on anomaly
    public void detect(Long userId, Long fileId, String type, String description) {
        AnomalyAlert alert = new AnomalyAlert();
        alert.setUserId(userId);
        alert.setFileId(fileId);
        alert.setType(type);
        alert.setDescription(description);
        alert.setDetectedAt(LocalDateTime.now());
        anomalyRepo.save(alert);

        System.out.println("🚨 SECURITY ALERT: " + description);
        emailService.sendAdminAlert("🚨 Security Alert: " + type, description);
    }

    // ✅ Get all anomalies
    public List<AnomalyAlert> getAllAlerts() {
        return anomalyRepo.findAll();
    }

    // ✅ Get anomalies by user
    public List<AnomalyAlert> getAlertsByUser(Long userId) {
        return anomalyRepo.findByUserId(userId);
    }
}
