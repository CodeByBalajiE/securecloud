package com.example.securecloud.service;

import com.example.securecloud.dto.AdminAnalyticsDTO;
import com.example.securecloud.model.AuditLog;
import com.example.securecloud.model.RevokedAccess;
import com.example.securecloud.model.EncryptedFile;
import com.example.securecloud.repository.FileRepository;
import com.example.securecloud.repository.RevokedAccessRepository;
import com.example.securecloud.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private FileRepository fileRepo;
    @Autowired private RevokedAccessRepository revokedRepo;
    @Autowired private AuditService auditService;

    public AdminAnalyticsDTO generateAnalytics() {
        AdminAnalyticsDTO dto = new AdminAnalyticsDTO();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 🔹 1. Files uploaded per day
        Map<String, Long> uploadsPerDay = fileRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        f -> f.getUploadedAt().format(formatter),
                        Collectors.counting()
                ));
        dto.setUploadsPerDay(uploadsPerDay);

        // 🔹 2. Revoked access per day
        Map<String, Long> revokedPerDay = revokedRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRevokedAt().format(formatter),
                        Collectors.counting()
                ));
        dto.setRevokedPerDay(revokedPerDay);

        // 🔹 3. OTP requests per day (from audit logs)
        List<AuditLog> logs = auditService.getAllLogs();

        Map<String, Long> otpRequestsPerDay = logs.stream()
                .filter(l -> l.getAction().equalsIgnoreCase("REQUEST_OTP"))
                .collect(Collectors.groupingBy(
                        l -> l.getCreatedAt().format(formatter),
                        Collectors.counting()
                ));
        dto.setOtpRequestsPerDay(otpRequestsPerDay);

        // 🔹 4. Total failed access attempts
        dto.setTotalFailedAccess(
                logs.stream().filter(l -> l.getAction().contains("ACCESS_DENIED")).count()
        );

        // 🔹 5. Total successful downloads
        dto.setTotalDownloads(
                logs.stream().filter(l -> l.getAction().equalsIgnoreCase("DOWNLOAD_SUCCESS")).count()
        );

        return dto;
    }
}
