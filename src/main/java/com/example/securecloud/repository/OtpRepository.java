package com.example.securecloud.repository;

import com.example.securecloud.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByUserIdAndFileId(Long userId, Long fileId);
    void deleteByUserIdAndFileId(Long userId, Long fileId);
}
