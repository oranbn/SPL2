package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private final AtomicInteger CPU_processedDataBatches;
    private final AtomicInteger CPU_timeUnitUsed;
    private final AtomicInteger GPU_timeUnitUsed;

    public synchronized int getCPU_processedDataBatches() {
        return CPU_processedDataBatches.get();
    }

    public synchronized void setCPU_processedDataBatches() {
        int value;
        do {
           value = this.CPU_processedDataBatches.get();
        }
        while (!CPU_processedDataBatches.compareAndSet(value,value+1));
    }

    public synchronized int getCPU_timeUnitUsed() {
        return CPU_timeUnitUsed.get();
    }

    public synchronized void setCPU_timeUnitUsed() {
        int value;
        do {
            value = this.CPU_timeUnitUsed.get();
        }
        while (!CPU_timeUnitUsed.compareAndSet(value,value+1));
    }

    public synchronized int getGPU_timeUnitUsed() {
        return GPU_timeUnitUsed.get();
    }

    public synchronized void setGPU_timeUnitUsed() {
        int value;
        do {
            value = GPU_timeUnitUsed.get();
        }
        while (!GPU_timeUnitUsed.compareAndSet(value,value+1));
    }

    public Statistics(){
        CPU_processedDataBatches = new AtomicInteger(0);
        CPU_timeUnitUsed = new AtomicInteger(0);
        GPU_timeUnitUsed = new AtomicInteger(0);


    }
}
