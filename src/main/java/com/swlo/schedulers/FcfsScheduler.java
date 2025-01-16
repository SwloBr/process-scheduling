package com.swlo.schedulers;

import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

import java.util.Comparator;
import java.util.List;

public class FcfsScheduler  extends  AbstractScheduler{


    public FcfsScheduler(boolean isPreemptive) {
        super("First-Come, First-Served", isPreemptive);

        if (isPreemptive) {
            throw new IllegalArgumentException("First-Come, First-Served cannot be preemptive");
        }
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;

        for (Process process : processes) {
            if (process.getArrivalTime() > currentTime) {
                currentTime = process.getArrivalTime();
            }

            int waitingTime = Math.max(0, currentTime - process.getArrivalTime());
            int turnaroundTime = waitingTime + process.getBurstTime();
            currentTime += process.getBurstTime();

            getLogger().addResultDetails(new ProcessResultDetails(
                    process.getId(),
                    process.getArrivalTime(),
                    process.getBurstTime(),
                    process.getPriority(),
                    waitingTime,
                    turnaroundTime
            ));
        }
    }
}
