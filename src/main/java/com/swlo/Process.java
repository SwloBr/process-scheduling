package com.swlo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Process {

    String id;
    int arrivalTime;
    int burstTime;
    int priority;

    private int originalBurstTime;

    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.originalBurstTime = burstTime;
    }
}
