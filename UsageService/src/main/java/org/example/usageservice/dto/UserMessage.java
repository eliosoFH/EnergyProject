package org.example.usageservice.dto;

import java.time.LocalDateTime;

public class UserMessage {
    private LocalDateTime timestamp;
    private double communityUsed;
    private double gridUsed;

    public UserMessage() {}

    public UserMessage(LocalDateTime timestamp, double communityUsed, double gridUsed) {
        this.timestamp = timestamp;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}
