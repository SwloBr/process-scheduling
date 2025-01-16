package com.swlo.schedulers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

public class RoundRobinScheduler extends AbstractScheduler {

    private final int timeQuantum;

    public RoundRobinScheduler(int timeQuantum) {
        super("Round Robin", true); // Round Robin é sempre preemptivo
        if (timeQuantum <= 0) {
            throw new IllegalArgumentException("Time quantum must be greater than 0");
        }
        this.timeQuantum = timeQuantum;
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        // Ordenar os processos por tempo de chegada
        processes.sort((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()));

        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int completedProcesses = 0;
        int n = processes.size();

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

            // Selecionar o próximo processo na fila de prontos
            Process currentProcess = readyQueue.poll();

            int executionTime = Math.min(currentProcess.getBurstTime(), timeQuantum);

            // Atualizar o tempo atual com o tempo de execução
            currentTime += executionTime;

            // Atualizar burst time restante do processo
            currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);

            // Se o processo ainda não terminou, adicioná-lo novamente à fila
            if (currentProcess.getBurstTime() > 0) {
                readyQueue.add(currentProcess);
            } else {
                // Processo concluído
                int waitingTime = currentTime - currentProcess.getArrivalTime() - currentProcess.getOriginalBurstTime();
                int turnaroundTime = waitingTime + currentProcess.getOriginalBurstTime();

                getLogger().addResultDetails(new ProcessResultDetails(
                        currentProcess.getId(),
                        currentProcess.getArrivalTime(),
                        currentProcess.getOriginalBurstTime(),
                        currentProcess.getPriority(),
                        waitingTime,
                        turnaroundTime
                ));

                completedProcesses++;
            }

            // Adicionar novos processos que chegaram enquanto o processo atual era executado
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }
        }
    }

}