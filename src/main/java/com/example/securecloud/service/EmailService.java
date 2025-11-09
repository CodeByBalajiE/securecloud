package com.example.securecloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.admin.email}")
    private String adminEmail; // 👈 Add this in application.properties

    @Async
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Your SecureCloud OTP");
        msg.setText("Your OTP: " + otp + "\nValid for 5 minutes.");
        mailSender.send(msg);
    }

    @Async
    public void sendAlert(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    // ✅ New method for admin alerts
    @Async
    public void sendAdminAlert(String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
        System.out.println("📧 Sent admin alert: " + subject);
    }
}
