package com.example.securecloud.repository;

import com.example.securecloud.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByFileId(Long fileId);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}
