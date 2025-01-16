package com.swlo.utils;

public record ProcessResultDetails(String id, int arrivalTime, int burstTime, int priority, int waitingTime, int turnaroundTime) {
}
