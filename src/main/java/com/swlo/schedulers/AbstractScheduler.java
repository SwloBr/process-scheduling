package com.swlo.schedulers;

import com.swlo.Process;
import com.swlo.utils.ProcessLogger;
import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractScheduler {


    private String name;
    private final boolean isPreemptive;

    private ProcessLogger logger;


    public AbstractScheduler(String name, boolean isPreemptive) {
        this.isPreemptive = isPreemptive;

        this.logger = new ProcessLogger(name, isPreemptive);
    }


    public abstract void run(List<Process> processes);

    public String log() {
        return logger.log();
    }

}
