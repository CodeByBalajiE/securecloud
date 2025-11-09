package com.example.securecloud.repository;

import com.example.securecloud.model.EncryptedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<EncryptedFile, Long> {
    List<EncryptedFile> findByUserId(Long userId);
}
