package com.example.securecloud.repository;

import com.example.securecloud.model.RevokedAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RevokedAccessRepository extends JpaRepository<RevokedAccess, Long> {
    boolean existsByUserIdAndFileId(Long userId, Long fileId);
    List<RevokedAccess> findByFileId(Long fileId);
    void deleteByUserIdAndFileId(Long userId, Long fileId);
}

