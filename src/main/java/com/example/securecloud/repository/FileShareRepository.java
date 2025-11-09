package com.example.securecloud.repository;

import com.example.securecloud.model.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    List<FileShare> findByFileId(Long fileId);
    List<FileShare> findByUserId(Long userId);
    boolean existsByFileIdAndUserId(Long fileId, Long userId);
}
