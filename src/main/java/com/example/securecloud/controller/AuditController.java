package com.example.securecloud.controller;

import com.example.securecloud.model.AuditLog;
import com.example.securecloud.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    // ✅ Get all audit logs
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }

    // ✅ Get logs for a specific user
    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getLogsByUser(userId));
    }

    // ✅ Get logs for a specific file
    @GetMapping("/logs/file/{fileId}")
    public ResponseEntity<List<AuditLog>> getLogsByFile(@PathVariable Long fileId) {
        return ResponseEntity.ok(auditService.getLogsByFile(fileId));
    }
}
