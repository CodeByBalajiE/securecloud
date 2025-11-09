package com.example.securecloud.controller;

import com.example.securecloud.model.OtpVerification;
import com.example.securecloud.service.AuditService;
import com.example.securecloud.service.EmailService;
import com.example.securecloud.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuditService auditService;

    // ✅ Generate new OTP
    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestParam Long userId, @RequestParam Long fileId, @RequestParam String email) {
        String otp = otpService.generateOtp(userId, fileId);
        emailService.sendOtpEmail(email, otp);
        auditService.log(userId, fileId, "REQUEST_OTP", "Generated new OTP and emailed to user");
        return ResponseEntity.ok("OTP sent successfully to: " + email);
    }

    // ✅ Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam Long userId, @RequestParam Long fileId, @RequestParam String otp) {
        boolean valid = otpService.verifyOtp(userId, fileId, otp);
        if (valid) {
            auditService.log(userId, fileId, "VERIFY_OTP", "OTP verified successfully");
            return ResponseEntity.ok("OTP verified successfully!");
        } else {
            auditService.log(userId, fileId, "VERIFY_OTP_FAILED", "Invalid or expired OTP");
            return ResponseEntity.status(401).body("Invalid or expired OTP");
        }
    }

    // ✅ Resend OTP
    @PostMapping("/resend")
    public ResponseEntity<String> resendOtp(@RequestParam Long userId, @RequestParam Long fileId, @RequestParam String email) {
        String otp = otpService.resendOtp(userId, fileId);
        emailService.sendOtpEmail(email, otp);
        auditService.log(userId, fileId, "RESEND_OTP", "Resent new OTP to user");
        return ResponseEntity.ok("New OTP sent to " + email);
    }

    // ✅ Get OTP Status
    @GetMapping("/status")
    public ResponseEntity<?> getOtpStatus(@RequestParam Long userId, @RequestParam Long fileId) {
        Optional<OtpVerification> otp = otpService.getOtpStatus(userId, fileId);
        if (otp.isEmpty()) {
            return ResponseEntity.status(404).body("No OTP found for this user and file");
        }
        return ResponseEntity.ok(otp.get());
    }
}
