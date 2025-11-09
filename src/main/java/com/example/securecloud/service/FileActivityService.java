package com.example.securecloud.service;

import com.example.securecloud.model.FileActivity;
import com.example.securecloud.repository.FileActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileActivityService {

    @Autowired
    private FileActivityRepository activityRepo;

    // Log a new activity
    public void log(Long userId, Long fileId, String action, String details) {
        FileActivity fa = new FileActivity();
        fa.setUserId(userId);
        fa.setFileId(fileId);
        fa.setAction(action);
        fa.setDetails(details);
        fa.setTimestamp(LocalDateTime.now());
        activityRepo.save(fa);
        System.out.println("📘 [Activity] " + action + " | User=" + userId + " | File=" + fileId);
    }

    // Get all activities for a file
    public List<FileActivity> getByFile(Long fileId) {
        return activityRepo.findByFileIdOrderByTimestampDesc(fileId);
    }

    // Get all activities for a user
    public List<FileActivity> getByUser(Long userId) {
        return activityRepo.findByUserIdOrderByTimestampDesc(userId);
    }

    // Get all activities
    public List<FileActivity> getAll() {
        return activityRepo.findAll();
    }
}
