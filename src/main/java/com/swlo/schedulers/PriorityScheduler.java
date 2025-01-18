package com.swlo.schedulers;

import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PriorityScheduler extends AbstractScheduler {

    public PriorityScheduler(boolean isPreemptive) {
        super("Priority Scheduler", isPreemptive);
    }


    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        PriorityQueue<Process> processQueue = new PriorityQueue<>(processes);

        if (isPreemptive()) {
            getLogger().setResultDetails(preemptivePriorityScheduler(processQueue));
        } else {
            getLogger().setResultDetails(nonPreemptivePriorityScheduler(processQueue));
        }


    }

    public static List<ProcessResultDetails> nonPreemptivePriorityScheduler(PriorityQueue<Process> processes) {
        List<ProcessResultDetails> results = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty()) {
            Process currentProcess = processes.poll();

            if (currentTime < currentProcess.getArrivalTime()) {
                currentTime = currentProcess.getArrivalTime();
            }

            int waitingTime = currentTime - currentProcess.getArrivalTime();
            currentTime += currentProcess.getBurstTime();
            int turnaroundTime = currentTime - currentProcess.getArrivalTime();

            results.add(new ProcessResultDetails(
                    currentProcess.getId(),
                    currentProcess.getArrivalTime(),
                    currentProcess.getBurstTime(),
                    currentProcess.getPriority(),
                    waitingTime,
                    turnaroundTime
            ));


            try {
                Thread.sleep(currentProcess.getBurstTime() * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

        return results;
    }

    public static List<ProcessResultDetails> preemptivePriorityScheduler(PriorityQueue<Process> processes) {
        List<ProcessResultDetails> results = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty()) {
            Process currentProcess = processes.poll();

            if (currentTime < currentProcess.getArrivalTime()) {
                currentTime = currentProcess.getArrivalTime();
            }


            try {
                Thread.sleep(1000L); // Simular execução de 1 unidade de tempo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            currentProcess.reduceRemainingTime(1);
            currentTime++;

            if (currentProcess.getRemainingTime() > 0) {
                processes.add(currentProcess);
            } else {
                int waitingTime = currentTime - currentProcess.getArrivalTime() - currentProcess.getBurstTime();
                int turnaroundTime = currentTime - currentProcess.getArrivalTime();

                results.add(new ProcessResultDetails(
                        currentProcess.getId(),
                        currentProcess.getArrivalTime(),
                        currentProcess.getBurstTime(),
                        currentProcess.getPriority(),
                        waitingTime,
                        turnaroundTime
                ));

            }
        }

        return results;
    }


}
