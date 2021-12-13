package bgu.spl.mics.application.objects.jsonObjects;

import bgu.spl.mics.application.objects.Statistics;

import java.util.ArrayList;
import java.util.List;

public class JsonOutput {
    private final List<JsonStudent> students;
    private final List<JsonConference> conferences;
    private final int cpuTimeUsed;
    private final int gpuTimeUsed;
    private final int batchesProcessed;

    public JsonOutput() {
        this.students = new ArrayList<>();
        conferences = new ArrayList<>();
        cpuTimeUsed = Statistics.getInstance().getCPU_timeUnitUsed();
        gpuTimeUsed = Statistics.getInstance().getGPU_timeUnitUsed();
        batchesProcessed = Statistics.getInstance().getCPU_processedDataBatches();
    }

    public List<JsonStudent> getStudents() {
        return students;
    }
    public void addStudent(JsonStudent student) {students.add(student);}
    public List<JsonConference> getConferences() {return conferences;}
    public void addConference(JsonConference conference) {conferences.add(conference);}
    public int getCpuTimeUsed() {
        return cpuTimeUsed;
    }
    public int getGpuTimeUsed() {
        return gpuTimeUsed;
    }
    public int getBatchesProcessed() {
        return batchesProcessed;
    }
}
