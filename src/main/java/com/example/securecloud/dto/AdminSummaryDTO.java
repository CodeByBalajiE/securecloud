package com.example.securecloud.dto;

public class AdminSummaryDTO {
    private long totalFiles;
    private long totalUsers;
    private long totalRevoked;
    private long totalDownloads;
    private long failedAttempts;
    private long totalOtpRequests;

    // Getters and Setters
    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalRevoked() {
        return totalRevoked;
    }

    public void setTotalRevoked(long totalRevoked) {
        this.totalRevoked = totalRevoked;
    }

    public long getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(long totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public long getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(long failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public long getTotalOtpRequests() {
        return totalOtpRequests;
    }

    public void setTotalOtpRequests(long totalOtpRequests) {
        this.totalOtpRequests = totalOtpRequests;
    }
}
