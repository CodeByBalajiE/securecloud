package com.example.securecloud.repository;

import com.example.securecloud.model.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {

    // 🔍 Find share link by unique code (only if active)
    Optional<ShareLink> findByUniqueCodeAndActiveTrue(String uniqueCode);
}
