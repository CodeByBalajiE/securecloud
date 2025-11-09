package com.example.securecloud.service;

import com.example.securecloud.model.ShareLink;
import com.example.securecloud.repository.ShareLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareLinkService {

    @Autowired
    private ShareLinkRepository repo;

    // ✅ Create a new share link for a file
    public String createShareLink(Long fileId, Long ownerId) {
        ShareLink link = new ShareLink();
        link.setFileId(fileId);
        link.setOwnerId(ownerId);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusHours(24)); // valid for 24 hours
        link.setUniqueCode(UUID.randomUUID().toString().substring(0, 8));
        link.setActive(true);
        repo.save(link);
        return "http://localhost:8080/api/files/share/" + link.getUniqueCode();
    }

    // ✅ Validate link (active and not expired)
    public Optional<ShareLink> validateShareLink(String code) {
        return repo.findByUniqueCodeAndActiveTrue(code)
                .filter(link -> link.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    // ✅ Auto-expire outdated links
    public void cleanupExpiredLinks() {
        repo.findAll().forEach(link -> {
            if (link.getExpiresAt().isBefore(LocalDateTime.now()) && link.isActive()) {
                link.setActive(false);
                repo.save(link);
            }
        });
    }
}
