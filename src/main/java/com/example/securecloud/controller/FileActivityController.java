package com.example.securecloud.controller;

import com.example.securecloud.model.FileActivity;
import com.example.securecloud.service.FileActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class FileActivityController {

    @Autowired
    private FileActivityService activityService;

    // ✅ Get activity timeline for a file
    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<FileActivity>> getFileActivity(@PathVariable Long fileId) {
        return ResponseEntity.ok(activityService.getByFile(fileId));
    }

    // ✅ Get activity timeline for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FileActivity>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(activityService.getByUser(userId));
    }

    // ✅ Get all activities (admin view)
    @GetMapping("/all")
    public ResponseEntity<List<FileActivity>> getAllActivity() {
        return ResponseEntity.ok(activityService.getAll());
    }
}
