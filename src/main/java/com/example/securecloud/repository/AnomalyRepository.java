package com.example.securecloud.repository;

import com.example.securecloud.model.AnomalyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnomalyRepository extends JpaRepository<AnomalyAlert, Long> {
    List<AnomalyAlert> findByUserId(Long userId);
}
