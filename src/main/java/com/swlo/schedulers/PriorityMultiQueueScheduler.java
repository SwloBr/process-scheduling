package com.swlo.schedulers;

import java.util.*;
import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

public class PriorityMultiQueueScheduler extends AbstractScheduler {

    private final int numberOfQueues;

    public PriorityMultiQueueScheduler(boolean isPreemptive, int numberOfQueues) {
        super("Priority Scheduling - Multiple Queues", isPreemptive);
        if (numberOfQueues <= 0) {
            throw new IllegalArgumentException("Number of queues must be greater than 0");
        }
        this.numberOfQueues = numberOfQueues;
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        // Criar filas de prioridade
        List<PriorityQueue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < numberOfQueues; i++) {
            queues.add(new PriorityQueue<>());
        }

        // Adicionar processos às filas (circularmente ou com base em lógica definida)
        for (Process process : processes) {
            int queueIndex = process.getPriority() % numberOfQueues; // Distribuir pelas filas
            queues.get(queueIndex).add(process);
        }

        // Executar escalonador
        if (isPreemptive()) {
            getLogger().setResultDetails(priorityMultiQueueScheduler(queues, true));
        } else {
            getLogger().setResultDetails(priorityMultiQueueScheduler(queues, false));
        }
    }


    public static List<ProcessResultDetails> priorityMultiQueueScheduler(List<PriorityQueue<Process>> queues, boolean isPreemptive) {
        List<ProcessResultDetails> results = new ArrayList<>();
        int currentTime = 0;

        while (queues.stream().anyMatch(queue -> !queue.isEmpty())) {
            for (PriorityQueue<Process> queue : queues) {
                if (!queue.isEmpty()) {
                    Process currentProcess = queue.poll();

                    if (currentTime < currentProcess.getArrivalTime()) {
                        currentTime = currentProcess.getArrivalTime();
                    }

                    if (isPreemptive) {
                        try {
                            Thread.sleep(1000L); // Simular execução de 1 unidade de tempo
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        currentProcess.reduceRemainingTime(1);
                        currentTime++;

                        if (currentProcess.getRemainingTime() > 0) {
                            queue.add(currentProcess);
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
                    } else {
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

                    break; // Prioridade mais alta sempre executa primeiro
                }
            }
        }

        return results;
    }
}
