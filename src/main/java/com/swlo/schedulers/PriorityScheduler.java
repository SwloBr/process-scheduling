package com.swlo.schedulers;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

public class PriorityScheduler extends AbstractScheduler {

    private final boolean isPreemptive;

    public PriorityScheduler(boolean isPreemptive) {
        super("Priority Scheduling", isPreemptive);
        this.isPreemptive = isPreemptive;
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        // Ordenar os processos por tempo de chegada inicialmente
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        int completedProcesses = 0;
        int n = processes.size();

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getPriority)
                        .thenComparingInt(Process::getArrivalTime)
        );

        List<Process> remainingProcesses = new ArrayList<>(processes);

        while (completedProcesses < n) {
            // Adicionar processos que chegaram até o tempo atual na fila de prontos
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                // CPU ociosa: avançar o tempo para o próximo processo que chega
                currentTime = remainingProcesses.get(0).getArrivalTime();
                continue;
            }

            // Selecionar o processo com maior prioridade (menor valor de prioridade)
            Process currentProcess = readyQueue.poll();

            if (isPreemptive) {
                int executionTime = 1; // Executar por um ciclo de tempo
                currentTime += executionTime;
                currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);

                if (currentProcess.getBurstTime() > 0) {
                    // Adicionar novamente se o processo não terminou
                    readyQueue.add(currentProcess);
                } else {
                    // Processo concluído
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

            // Adicionar novos processos que chegaram enquanto o processo atual era executado
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
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
