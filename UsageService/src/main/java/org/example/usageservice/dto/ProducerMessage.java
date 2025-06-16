package org.example.usageservice.dto;

import java.time.LocalDateTime;

public class ProducerMessage {
    private LocalDateTime timestamp;
    private double energy;

    public ProducerMessage() {}

    public ProducerMessage(LocalDateTime timestamp, double energy) {
        this.timestamp = timestamp;
        this.energy = energy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
