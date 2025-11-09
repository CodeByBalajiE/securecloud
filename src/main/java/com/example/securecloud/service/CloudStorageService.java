package com.example.securecloud.service;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CloudStorageService {

    private static final Path UPLOAD_DIR = Paths.get("uploads");

    public String uploadFile(byte[] fileBytes, String originalFilename) throws IOException {
        if (!Files.exists(UPLOAD_DIR)) Files.createDirectories(UPLOAD_DIR);
        String uniqueName = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = UPLOAD_DIR.resolve(uniqueName);
        Files.write(filePath, fileBytes);
        return filePath.toString();
    }

    public byte[] downloadFile(String storagePath) throws IOException {
        Path path = Paths.get(storagePath);
        if (!Files.exists(path))
            throw new FileNotFoundException("❌ File missing on disk: " + path);
        return Files.readAllBytes(path);
    }
}

