package com.example.currentenergypercentage.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentageEntity {
    @Id
    @Column
    private LocalDateTime hour;

    @Column
    private double community_depleted;

    @Column
    private double community_used;

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunity_depleted() {
        return community_depleted;
    }

    public void setCommunity_depleted(double community_depleted) {
        this.community_depleted = community_depleted;
    }

    public double getCommunity_used() {
        return community_used;
    }

    public void setCommunity_used(double community_used) {
        this.community_used = community_used;
    }

    public CurrentPercentageEntity() {}
    public CurrentPercentageEntity(LocalDateTime hour, double community_depleted, double community_used) {
        this.hour = hour;
        this.community_depleted = community_depleted;
        this.community_used = community_used;
    }
}
