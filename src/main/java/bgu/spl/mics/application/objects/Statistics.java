package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private static class StatisticsHolder{
        private final static Statistics instance = new Statistics();
    }

    private int CPU_processedDataBatches;
    private int CPU_timeUnitUsed;
    private int GPU_timeUnitUsed;

    public synchronized int getCPU_processedDataBatches() {
        return CPU_processedDataBatches;
    }

    public synchronized void setCPU_processedDataBatches(int CPU_processedDataBatches) {
        this.CPU_processedDataBatches = CPU_processedDataBatches;
    }

    public synchronized int getCPU_timeUnitUsed() {
        return CPU_timeUnitUsed;
    }

    public synchronized void setCPU_timeUnitUsed(int CPU_timeUnitUsed) {
        this.CPU_timeUnitUsed = CPU_timeUnitUsed;
    }

    public synchronized int getGPU_timeUnitUsed() {
        return GPU_timeUnitUsed;
    }

    public synchronized void setGPU_timeUnitUsed(int GPU_timeUnitUsed) {
        this.GPU_timeUnitUsed = GPU_timeUnitUsed;
    }

    private Statistics(){
        CPU_processedDataBatches = 0;
        CPU_timeUnitUsed = 0;
        GPU_timeUnitUsed = 0;


    }

    public static Statistics getInstance(){
        return StatisticsHolder.instance;
    }
}
