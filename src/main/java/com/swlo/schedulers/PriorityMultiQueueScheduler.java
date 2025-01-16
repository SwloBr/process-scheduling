package com.swlo.schedulers;

import java.util.*;

import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

public class PriorityMultiQueueScheduler extends AbstractScheduler {

    private final boolean isPreemptive;
    private final int numberOfQueues;

    public PriorityMultiQueueScheduler(boolean isPreemptive, int numberOfQueues) {
        super("Priority Scheduling - Multiple Queues", isPreemptive);
        if (numberOfQueues <= 0) {
            throw new IllegalArgumentException("Number of queues must be greater than 0");
        }
        this.isPreemptive = isPreemptive;
        this.numberOfQueues = numberOfQueues;
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        // Ordenar os processos por tempo de chegada inicialmente
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<Queue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < numberOfQueues; i++) {
            queues.add(new LinkedList<>());
        }

        // Adicionar processos às filas com base na prioridade (simulada por níveis de prioridade)
        for (Process process : processes) {
            int queueIndex = Math.min(process.getPriority() - 1, numberOfQueues - 1);
            queues.get(queueIndex).add(process);
        }

        int currentTime = 0;
        int completedProcesses = 0;
        int n = processes.size();

        while (completedProcesses < n) {
            boolean processExecuted = false;

            for (int i = 0; i < numberOfQueues; i++) {
                Queue<Process> queue = queues.get(i);

                if (!queue.isEmpty()) {
                    Process currentProcess = queue.poll();

                    if (isPreemptive) {
                        int executionTime = 1; // Executar por um ciclo de tempo
                        currentTime += executionTime;
                        currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);

                        if (currentProcess.getBurstTime() > 0) {
                            queue.add(currentProcess);
                        } else {
                            completedProcesses++;
                            logProcessCompletion(currentProcess, currentTime);
                        }
                    } else {
                        // Não preemptivo: Executar o processo inteiro
                        currentTime += currentProcess.getBurstTime();
                        currentProcess.setBurstTime(0);
                        completedProcesses++;
                        logProcessCompletion(currentProcess, currentTime);
                    }

                    processExecuted = true;
                    break;
                }
            }

            if (!processExecuted) {
                // CPU ociosa: avançar o tempo para o próximo processo
                currentTime++;
            }
        }
    }

    private void logProcessCompletion(Process process, int completionTime) {
        int waitingTime = completionTime - process.getArrivalTime() - process.getOriginalBurstTime();
        int turnaroundTime = waitingTime + process.getOriginalBurstTime();

        getLogger().addResultDetails(new ProcessResultDetails(
                process.getId(),
                process.getArrivalTime(),
                process.getOriginalBurstTime(),
                process.getPriority(),
                waitingTime,
                turnaroundTime
        ));
    }
}
