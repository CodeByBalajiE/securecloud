package com.example.securecloud.dto;

import java.util.Map;

public class AdminAnalyticsDTO {
    private Map<String, Long> uploadsPerDay;
    private Map<String, Long> otpRequestsPerDay;
    private Map<String, Long> revokedPerDay;
    private long totalFailedAccess;
    private long totalDownloads;

    // Getters and Setters
    public Map<String, Long> getUploadsPerDay() {
        return uploadsPerDay;
    }

    public void setUploadsPerDay(Map<String, Long> uploadsPerDay) {
        this.uploadsPerDay = uploadsPerDay;
    }

    public Map<String, Long> getOtpRequestsPerDay() {
        return otpRequestsPerDay;
    }

    public void setOtpRequestsPerDay(Map<String, Long> otpRequestsPerDay) {
        this.otpRequestsPerDay = otpRequestsPerDay;
    }

    public Map<String, Long> getRevokedPerDay() {
        return revokedPerDay;
    }

    public void setRevokedPerDay(Map<String, Long> revokedPerDay) {
        this.revokedPerDay = revokedPerDay;
    }

    public long getTotalFailedAccess() {
        return totalFailedAccess;
    }

    public void setTotalFailedAccess(long totalFailedAccess) {
        this.totalFailedAccess = totalFailedAccess;
    }

    public long getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(long totalDownloads) {
        this.totalDownloads = totalDownloads;
    }
}
