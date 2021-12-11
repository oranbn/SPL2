package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private static class StatisticsHolder{
        private static Statistics instance = new Statistics();
    }

    private final List<String> modelNameList;
    int CPU_processedDataBatches;
    int CPU_timeUnitUsed;
    int GPU_timeUnitUsed;

    private Statistics(){
        modelNameList = new ArrayList<>();
        CPU_processedDataBatches = 0;
        CPU_timeUnitUsed = 0;
        GPU_timeUnitUsed = 0;


    }

    public static Statistics getInstance(){
        return StatisticsHolder.instance;
    }
}
