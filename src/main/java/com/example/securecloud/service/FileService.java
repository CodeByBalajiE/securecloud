package com.example.securecloud.service;

import com.example.securecloud.model.EncryptedFile;
import com.example.securecloud.model.RevokedAccess;
import com.example.securecloud.model.Role;
import com.example.securecloud.model.User;
import com.example.securecloud.repository.FileRepository;
import com.example.securecloud.repository.RevokedAccessRepository;
import com.example.securecloud.repository.UserRepository;
import com.example.securecloud.util.AESUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import com.example.securecloud.model.EncryptedFile;
import com.example.securecloud.model.RevokedAccess;
import com.example.securecloud.model.Role;
import com.example.securecloud.model.User;
import com.example.securecloud.repository.FileRepository;
import com.example.securecloud.repository.RevokedAccessRepository;
import com.example.securecloud.repository.UserRepository;
import com.example.securecloud.util.AESUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired private FileActivityService activityService;
    @Autowired private SecurityMonitorService monitorService;
    @Autowired private FileRepository fileRepo;
    @Autowired private RevokedAccessRepository revokedRepo;
    @Autowired private CloudStorageService cloud;
    @Autowired private AuditService audit;
    @Autowired private OtpService otpService;
    @Autowired private UserRepository userRepo;
    @Autowired private EmailService emailService;

    @Value("${app.masterKeyBase64}")
    private String masterKey;

    // ======================================================
    // ✅ UPLOAD (Binary-safe AES encryption)
    // ======================================================
    public String uploadFile(MultipartFile file, Long userId, boolean otpRequired) {
        try {
            String uuid = UUID.randomUUID().toString();

            // 🔒 Encrypt file bytes (BINARY, not Base64)
            byte[] encryptedBytes = AESUtil.encrypt(file.getBytes(), masterKey, uuid);

            // ☁️ Upload encrypted file to disk/cloud storage
            String storagePath = cloud.uploadFile(encryptedBytes, file.getOriginalFilename());

            // 🗃 Save metadata in DB
            EncryptedFile ef = new EncryptedFile();
            ef.setOriginalFilename(file.getOriginalFilename());
            ef.setStoragePath(storagePath);
            ef.setUploadedAt(LocalDateTime.now());
            ef.setUserId(userId);
            ef.setEncryptionKey(uuid);
            ef.setEncrypted(true);
            ef.setOtpRequired(otpRequired);
            fileRepo.save(ef);
            // During upload

            // 🧾 Activity + Audit
            activityService.log(userId, ef.getId(), "UPLOAD", "Uploaded file: " + ef.getOriginalFilename());
            audit.log(userId, ef.getId(), "UPLOAD", "File uploaded (OTP required: " + otpRequired + ")");

            return "✅ File uploaded successfully! ID: " + ef.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Upload failed: " + e.getMessage();
        }
    }
    @PostConstruct
    public void initMasterKey() {
        try {
            if (masterKey == null || masterKey.isEmpty()) {
                throw new IllegalStateException("Master key not found in properties!");
            }

            // Decode Base64 into raw bytes
            byte[] decodedKey = java.util.Base64.getDecoder().decode(masterKey);

            // Validate AES key length
            if (!(decodedKey.length == 16 || decodedKey.length == 24 || decodedKey.length == 32)) {
                throw new IllegalArgumentException("Invalid AES key length: " + decodedKey.length + " bytes");
            }

            // Store raw bytes as ISO-safe string for AESUtil
            masterKey = new String(decodedKey, java.nio.charset.StandardCharsets.ISO_8859_1);

            System.out.println("🔐 AES Master Key decoded successfully (" + decodedKey.length * 8 + "-bit)");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize AES key: " + e.getMessage());
            throw new RuntimeException("Failed to initialize AES master key", e);
        }
    }



    // ======================================================
    // ✅ GENERATE OTP
    // ======================================================
    @Transactional
    public void generateOtp(Long userId, Long fileId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("⚠️ User not found. Creating temp user...");
            user = new User();
            user.setUsername("TempUser_" + System.currentTimeMillis());
            user.setEmail("tempuser" + System.currentTimeMillis() + "@example.com");
            user.setPassword("12345");
            user.setRole(Role.USER);
            userRepo.save(user);
        }

        String otp = otpService.generateOtp(user.getId(), fileId);
        try {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendOtpEmail(user.getEmail(), otp);
                audit.log(user.getId(), fileId, "OTP_SENT", "OTP sent to " + user.getEmail());
                System.out.println("📩 OTP sent to " + user.getEmail() + ": " + otp);
            } else {
                throw new RuntimeException("User email missing");
            }
        } catch (Exception e) {
            audit.log(user.getId(), fileId, "OTP_FAIL", "Email send failed for " + user.getEmail());
            System.err.println("❌ Failed to send OTP: " + e.getMessage());
        }
    }

    // ======================================================
    // ✅ SECURE DOWNLOAD (Binary-safe AES decryption)
    // ======================================================
    public byte[] secureDownloadFile(Long fileId, Long userId, String otp) throws Exception {
        EncryptedFile f = fileRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (revokedRepo.existsByUserIdAndFileId(userId, fileId)) {
            monitorService.detect(userId, fileId, "REVOKED_ACCESS", "User tried to download revoked file");
            throw new SecurityException("Access revoked for this file");
        }

        // OTP check
        if (f.isOtpRequired()) {
            if (otp == null || !otpService.verifyOtp(userId, fileId, otp)) {
                audit.log(userId, fileId, "ACCESS_DENIED", "Invalid or expired OTP");
                throw new SecurityException("Invalid or expired OTP");
            }
        }

        // ✅ Download encrypted file from cloud storage
        byte[] encryptedData = cloud.downloadFile(f.getStoragePath());
        byte[] decryptedData = AESUtil.decrypt(encryptedData, masterKey, f.getEncryptionKey());
        return decryptedData;// ✅ Must return decrypted, not encrypted
    }


    // ======================================================
    // ✅ REVOKE ACCESS
    // ======================================================
    public void revokeAccess(Long userId, Long fileId) {
        if (!revokedRepo.existsByUserIdAndFileId(userId, fileId)) {
            RevokedAccess ra = new RevokedAccess();
            ra.setUserId(userId);
            ra.setFileId(fileId);
            ra.setRevokedAt(LocalDateTime.now());
            revokedRepo.save(ra);
            audit.log(userId, fileId, "REVOKE", "User access revoked");
            activityService.log(userId, fileId, "REVOKE", "Access revoked for user " + userId);
        }
    }

    // ======================================================
    // ✅ REINSTATE ACCESS
    // ======================================================
    @Transactional
    public void reinstateAccess(Long userId, Long fileId) {
        revokedRepo.deleteByUserIdAndFileId(userId, fileId);
        audit.log(userId, fileId, "REINSTATE", "Access reinstated");
        activityService.log(userId, fileId, "REINSTATE", "Access reinstated for user " + userId);
    }

    // ======================================================
    // ✅ GETTERS
    // ======================================================
    public boolean isAccessRevoked(Long userId, Long fileId) {
        return revokedRepo.existsByUserIdAndFileId(userId, fileId);
    }

    public List<EncryptedFile> getFilesByUser(Long userId) {
        return fileRepo.findByUserId(userId);
    }

    public List<RevokedAccess> getAllRevokedAccess() {
        return revokedRepo.findAll();
    }
}
