package com.example.securecloud.repository;

import com.example.securecloud.model.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileActivityRepository extends JpaRepository<FileActivity, Long> {
    List<FileActivity> findByFileIdOrderByTimestampDesc(Long fileId);
    List<FileActivity> findByUserIdOrderByTimestampDesc(Long userId);
}
