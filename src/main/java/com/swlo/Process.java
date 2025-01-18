package com.swlo;

import lombok.Data;

@Data
public class Process implements Comparable<Process> {

    private String id;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingTime;

    private int originalBurstTime;

    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.originalBurstTime = burstTime;
    }


    public void reduceRemainingTime(int time) {
        this.remainingTime -= time;
    }

    @Override
    public int compareTo(Process other) {
        return Integer.compare(this.priority, other.priority);
    }
}
