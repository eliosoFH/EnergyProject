package com.example.currentenergypercentage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyUsageHourly {
    @JsonProperty("produced")
    private double communityProduced;
    @JsonProperty("used")
    private double communityUsed;
    @JsonProperty("grid")
    private double gritUsed;
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGritUsed() {
        return gritUsed;
    }

    public void setGritUsed(double gritUsed) {
        this.gritUsed = gritUsed;
    }

    @Override
    public String toString() {
        return "EnergyStats{" +
                "communityProduced=" + communityProduced +
                ", communityUsed=" + communityUsed +
                ", gritUsed=" + gritUsed +
                ", timestamp=" + timestamp +
                '}';
    }
}
