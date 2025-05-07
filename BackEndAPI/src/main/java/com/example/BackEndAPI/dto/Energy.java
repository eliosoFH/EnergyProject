package com.example.BackEndAPI.dto;

import java.time.LocalDateTime;

public class Energy {
    private int tid;
    private LocalDateTime time;
    private double energyCapacity;

    public Energy() {}

    public Energy(int tid, LocalDateTime time, double energyCapacity) {
        this.tid = tid;
        this.time = time;
        this.energyCapacity = energyCapacity;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getEnergyCapacity() {
        return energyCapacity;
    }

    public void setEnergyCapacity(double energyCapacity) {
        this.energyCapacity = energyCapacity;
    }

}
