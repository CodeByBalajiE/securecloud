package com.example.securecloud.controller;

import com.example.securecloud.dto.AdminSummaryDTO;
import com.example.securecloud.model.AuditLog;
import com.example.securecloud.model.RevokedAccess;
import com.example.securecloud.repository.FileRepository;
import com.example.securecloud.repository.RevokedAccessRepository;
import com.example.securecloud.repository.UserRepository;
import com.example.securecloud.service.AuditService;
import com.example.securecloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private FileRepository fileRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RevokedAccessRepository revokedRepo;

    @Autowired
    private AuditService auditService;

    @Autowired
    private FileService fileService;

    // ✅ Get audit logs by user
    @GetMapping("/audit/user/{userId}")
    public ResponseEntity<List<AuditLog>> userAudit(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getLogsByUser(userId));
    }

    // ✅ Get audit logs by file
    @GetMapping("/audit/file/{fileId}")
    public ResponseEntity<List<AuditLog>> fileAudit(@PathVariable Long fileId) {
        return ResponseEntity.ok(auditService.getLogsByFile(fileId));
    }

    // ✅ Get all revoked access records
    @GetMapping("/revoked")
    public ResponseEntity<List<RevokedAccess>> allRevoked() {
        return ResponseEntity.ok(fileService.getAllRevokedAccess());
    }
    @GetMapping("/summary")
    public ResponseEntity<AdminSummaryDTO> getSystemSummary() {
        AdminSummaryDTO summary = new AdminSummaryDTO();

        summary.setTotalFiles(fileRepo.count());
        summary.setTotalUsers(userRepo.count());
        summary.setTotalRevoked(revokedRepo.count());

        // From audit logs
        List<AuditLog> logs = auditService.getAllLogs();

        summary.setTotalDownloads(logs.stream().filter(l -> l.getAction().equalsIgnoreCase("DOWNLOAD_SUCCESS")).count());
        summary.setFailedAttempts(logs.stream().filter(l -> l.getAction().contains("ACCESS_DENIED")).count());
        summary.setTotalOtpRequests(logs.stream().filter(l -> l.getAction().equalsIgnoreCase("REQUEST_OTP")).count());

        return ResponseEntity.ok(summary);
    }

}
