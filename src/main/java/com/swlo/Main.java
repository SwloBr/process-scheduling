package com.swlo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.swlo.Process;
import com.swlo.schedulers.*;

public class Main {


    public static void main(String[] args) {
        // Criar processos de exemplo
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("1", 0, 5, 1)); // id, arrivalTime, burstTime, priority
        processes.add(new Process("2", 1, 3, 2));
        processes.add(new Process("3", 2, 8, 3));
        processes.add(new Process("4", 3, 6, 1));

        // Instanciar todos os schedulers disponíveis
        List<AbstractScheduler> schedulers = new ArrayList<>();
        schedulers.add(new FcfsScheduler(false));
        schedulers.add(new SjfScheduler(false));
        schedulers.add(new RoundRobinScheduler(2)); // time quantum = 2
        schedulers.add(new PriorityScheduler(false));
        schedulers.add(new PriorityMultiQueueScheduler(false, 3)); // 3 filas de prioridade
        schedulers.add(new LotteryScheduler(true));

        try (FileWriter writer = new FileWriter("schedulers_results.txt")) {
            for (AbstractScheduler scheduler : schedulers) {
                // Executar o scheduler
                scheduler.run(new ArrayList<>(processes));

                // Gravar os resultados no arquivo
                writer.write("------------------------\n");
                writer.write(scheduler.log());
                writer.write("\n------------------------\n\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever os resultados: " + e.getMessage());
        }

        System.out.println("Execução de todos os schedulers concluída. Resultados salvos em 'schedulers_results.txt'.");
    }




}