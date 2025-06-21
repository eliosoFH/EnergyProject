package com.example.frontend.dto;

import java.time.LocalDateTime;

public class CurrentPercentage {
    LocalDateTime hour;
    double communityDepleted;
    double gridPortion;

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    public CurrentPercentage(LocalDateTime hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public CurrentPercentage() {}

    @Override
    public String toString() {
        return "CurrentPercentage{" +
                "timestamp=" + hour +
                ", communityDepleted=" + communityDepleted +
                ", gridPortion=" + gridPortion +
                '}';
    }
}
