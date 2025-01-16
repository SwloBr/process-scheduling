package com.swlo.schedulers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;


public class SjfScheduler extends AbstractScheduler {

    public SjfScheduler(boolean isPreemptive) {
        super("Shortest Job First", isPreemptive);
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

        // Usar PriorityQueue para selecionar o próximo processo com menor burst time
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime));

        List<Process> remainingProcesses = new ArrayList<>(processes);

        while (completedProcesses < n) {
            // Adicionar todos os processos que chegaram até o tempo atual na fila de prontos
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                // CPU ociosa: avançar o tempo para o próximo processo que chega
                currentTime = remainingProcesses.get(0).getArrivalTime();
                continue;
            }

            // Selecionar o processo com menor burst time
            Process currentProcess = readyQueue.poll();

            // Calcular tempos
            int waitingTime = Math.max(0, currentTime - currentProcess.getArrivalTime());
            int turnaroundTime = waitingTime + currentProcess.getBurstTime();

            // Atualizar tempo atual
            currentTime += currentProcess.getBurstTime();

            // Armazenar os resultados no logger
            getLogger().addResultDetails(new ProcessResultDetails(
                    currentProcess.getId(),
                    currentProcess.getArrivalTime(),
                    currentProcess.getBurstTime(),
                    currentProcess.getPriority(),
                    waitingTime,
                    turnaroundTime
            ));

            completedProcesses++;
        }
    }

}