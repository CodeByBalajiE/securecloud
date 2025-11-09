package com.example.securecloud.service;

import com.example.securecloud.model.OtpVerification;
import com.example.securecloud.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    @Autowired private EmailService emailService;

    @Autowired
    private FileActivityService activityService;
    @Autowired
    private OtpRepository otpRepo;
    // ✅ Send OTP email
    public void sendOtpEmail(String email, String otp) {
        emailService.sendOtpEmail(email, otp);
    }

    // ✅ Generate new OTP
    public String generateOtp(Long userId, Long fileId) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        // Remove old OTP
        otpRepo.deleteByUserIdAndFileId(userId, fileId);

        OtpVerification o = new OtpVerification();
        o.setUserId(userId);
        o.setFileId(fileId);
        o.setOtp(otp);
        o.setExpiryTime(expiry);
        otpRepo.save(o);
        activityService.log(userId, fileId, "REQUEST_OTP", "New OTP generated for file access");

        return otp;
    }
    @Autowired
    private SecurityMonitorService monitorService;

    // ✅ Verify OTP validity
    public boolean verifyOtp(Long userId, Long fileId, String otp) {
        Optional<OtpVerification> existing = otpRepo.findByUserIdAndFileId(userId, fileId);
        if (existing.isEmpty()) {
            monitorService.detect(userId, fileId, "OTP_FAIL", "No OTP record found for user during verification");
            return false;
        }

        OtpVerification record = existing.get();
        if (!record.getOtp().equals(otp)) {
            monitorService.detect(userId, fileId, "OTP_FAIL", "Incorrect OTP entered by user " + userId);
            return false;
        }

        if (LocalDateTime.now().isAfter(record.getExpiryTime())) {
            monitorService.detect(userId, fileId, "OTP_EXPIRED", "Expired OTP used by user " + userId);
            return false;
        }

        return true;
    }

    // ✅ Resend OTP (new one replaces old)
    public String resendOtp(Long userId, Long fileId) {
        return generateOtp(userId, fileId);
    }

    // ✅ Get OTP status for user/file
    public Optional<OtpVerification> getOtpStatus(Long userId, Long fileId) {
        return otpRepo.findByUserIdAndFileId(userId, fileId);
    }
}
