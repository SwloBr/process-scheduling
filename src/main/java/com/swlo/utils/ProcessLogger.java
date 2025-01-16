package com.swlo.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessLogger {

    private String ALGORITHM_NAME;
    private boolean isPreemptive;
    private List<ProcessResultDetails> resultDetails;

    public ProcessLogger(String ALGORITHM_NAME, boolean isPreemptive) {
        this.ALGORITHM_NAME = ALGORITHM_NAME;
        this.isPreemptive = isPreemptive;
        this.resultDetails = new ArrayList<>();
    }

    public void addResultDetails(ProcessResultDetails processResultDetails) {
        resultDetails.add(processResultDetails);
    }

    public String log() {

        StringBuilder log = new StringBuilder();
        log.append("Algorithm: ").append(ALGORITHM_NAME).append("\n");
        log.append("Preemptive: ").append(isPreemptive).append("\n\n");

        String header = formatCenter(
                new String[]{"ID", "Arrival Time", "Burst Time", "Priority", "Waiting Time", "Turnaround Time"},
                new int[]{5, 12, 10, 8, 15, 18}
        );
        log.append(header).append("\n");
        log.append("-".repeat(header.length())).append("\n");

        for (ProcessResultDetails process : resultDetails) {
            String row = formatCenter(
                    new String[]{
                            String.valueOf(process.id()),
                            String.valueOf(process.arrivalTime()),
                            String.valueOf(process.burstTime()),
                            String.valueOf(process.priority()),
                            String.valueOf(process.waitingTime()),
                            String.valueOf(process.turnaroundTime())
                    },
                    new int[]{5, 12, 10, 8, 15, 18}
            );
            log.append(row).append("\n");
        }

        return log.toString();
    }

    private String formatCenter(String[] values, int[] widths) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            int width = widths[i];
            int padding = width - value.length();
            int leftPadding = padding / 2;
            int rightPadding = padding - leftPadding;
            result.append(" ".repeat(leftPadding)).append(value).append(" ".repeat(rightPadding));
            if (i < values.length - 1) {
                result.append(" | ");
            }
        }
        return result.toString();
        }
}
