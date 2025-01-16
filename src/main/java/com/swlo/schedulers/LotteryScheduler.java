package com.swlo.schedulers;


import com.swlo.Process;
import com.swlo.utils.ProcessResultDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LotteryScheduler extends AbstractScheduler {

    private final boolean isPreemptive;

    public LotteryScheduler(boolean isPreemptive) {
        super("Lottery Scheduling", isPreemptive);
        this.isPreemptive = isPreemptive;
    }

    @Override
    public void run(List<Process> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be null or empty");
        }

        // Inicializar a lista de tickets e atribuir tickets a cada processo
        List<Process> ticketPool = new ArrayList<>();
        for (Process process : processes) {
            for (int i = 0; i < process.getPriority(); i++) {
                ticketPool.add(process);
            }
        }

        int currentTime = 0;
        int completedProcesses = 0;
        int n = processes.size();
        Random random = new Random();

        while (completedProcesses < n) {
            if (ticketPool.isEmpty()) {
                // Avançar o tempo para o próximo processo que chega
                Process nextProcess = processes.stream()
                        .filter(p -> p.getBurstTime() > 0)
                        .min((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()))
                        .orElse(null);

                if (nextProcess == null) break;
                currentTime = nextProcess.getArrivalTime();
                continue;
            }

            // Selecionar um ticket aleatório
            Process selectedProcess = ticketPool.get(random.nextInt(ticketPool.size()));

            if (selectedProcess.getArrivalTime() > currentTime) {
                currentTime = selectedProcess.getArrivalTime();
            }

            if (isPreemptive) {
                // Executar por um ciclo de tempo
                int executionTime = 1;
                currentTime += executionTime;
                selectedProcess.setBurstTime(selectedProcess.getBurstTime() - executionTime);

                // Remover tickets se o processo terminou
                if (selectedProcess.getBurstTime() <= 0) {
                    completedProcesses++;
                    ticketPool.removeIf(p -> p == selectedProcess);
                    logProcessCompletion(selectedProcess, currentTime);
                }
            } else {
                // Não preemptivo: Executar o processo inteiro
                currentTime += selectedProcess.getBurstTime();
                selectedProcess.setBurstTime(0);
                completedProcesses++;
                ticketPool.removeIf(p -> p == selectedProcess);
                logProcessCompletion(selectedProcess, currentTime);
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
