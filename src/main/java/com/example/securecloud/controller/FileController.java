package com.example.securecloud.controller;

import com.example.securecloud.model.EncryptedFile;
import com.example.securecloud.model.RevokedAccess;
import com.example.securecloud.model.User;
import com.example.securecloud.repository.FileRepository;
import com.example.securecloud.repository.UserRepository;
import com.example.securecloud.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("*")
public class FileController {
    @Autowired private FileRepository fileRepo;
    @Autowired private FileService fileService;
    @Autowired private ShareLinkService shareLinkService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam Long userId,
                                             @RequestParam(defaultValue = "false") boolean otpRequired) {
        return ResponseEntity.ok(fileService.uploadFile(file, userId, otpRequired));
    }

    @GetMapping("/user/{userId}")
    public List<EncryptedFile> getFiles(@PathVariable Long userId) {
        return fileService.getFilesByUser(userId);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestParam Long userId, @RequestParam Long fileId) {
        fileService.generateOtp(userId, fileId);
        return ResponseEntity.ok("✅ OTP sent to registered email.");
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam Long fileId,
            @RequestParam Long userId,
            @RequestParam(required = false) String otp
    ) throws Exception {

        EncryptedFile fileMeta = fileRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found in DB"));

        byte[] decryptedData = fileService.secureDownloadFile(fileId, userId, otp);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileMeta.getOriginalFilename() + "\"")
                .header("Content-Type", "application/octet-stream")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body(decryptedData);
    }



    @PostMapping("/revoke")
    public ResponseEntity<String> revoke(@RequestParam Long userId, @RequestParam Long fileId) {
        fileService.revokeAccess(userId, fileId);
        return ResponseEntity.ok("🚫 Access revoked.");
    }

    @PostMapping("/reinstate")
    public ResponseEntity<String> reinstate(@RequestParam Long userId, @RequestParam Long fileId) {
        fileService.reinstateAccess(userId, fileId);
        return ResponseEntity.ok("✅ Access reinstated.");
    }

    // Create and Access Share Link
    @PostMapping("/share/create")
    public ResponseEntity<String> createShare(@RequestParam Long fileId, @RequestParam Long ownerId) {
        return ResponseEntity.ok(shareLinkService.createShareLink(fileId, ownerId));
    }

    @GetMapping("/share/{code}")
    public ResponseEntity<byte[]> accessShare(@PathVariable String code) throws Exception {
        var link = shareLinkService.validateShareLink(code)
                .orElseThrow(() -> new RuntimeException("Invalid or expired link"));
        byte[] data = fileService.secureDownloadFile(link.getFileId(), link.getOwnerId(), null);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SharedFile.bin\"")
                .body(data);
    }
}

