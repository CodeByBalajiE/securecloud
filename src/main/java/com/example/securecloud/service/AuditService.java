package com.example.securecloud.service;

import com.example.securecloud.model.AuditLog;
import com.example.securecloud.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditRepository repo;

    // ✅ Record any user/file action
    public void log(Long userId, Long fileId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setFileId(fileId);
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        repo.save(log);
    }

    // ✅ Get all logs
    public List<AuditLog> getAllLogs() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    // ✅ Get logs by specific user
    public List<AuditLog> getLogsByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    // ✅ Get logs by specific file
    public List<AuditLog> getLogsByFile(Long fileId) {
        return repo.findByFileId(fileId);
    }
}
