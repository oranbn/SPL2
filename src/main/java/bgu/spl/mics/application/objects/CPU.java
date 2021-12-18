package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 * @INV: ticks >= 0
 */
public class CPU {
    private int cores;
    private DataBatch dataBatch;
    private Cluster cluster;
    private int gpuID;
    private int ticks; //ticks left
    private boolean processing;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.cluster = cluster;
        this.ticks = 0;
        this.processing = false;
        Cluster.getInstance().addCPU(this);
    }
    /**
     * <p>
     * @return return the Data of DataBatch
     * @PRE: none
     * @POST: none
     */
    public Data getData() {
        return dataBatch.getData();
    }
    /**
     * <p>
     * @return return the Type of Data
     * @PRE: none
     * @POST: none
     */
    public Data.Type getType() {
        return getData().getType();
    }
    /**
     * <p>
     * @return return the boolean value of processing
     * @PRE: none
     * @POST: none
     */
    public boolean getProcessing() {
        return processing;
    }

    /**
     * <p>
     * @return return the DataBatch of the class
     * @PRE: none
     * @POST: none
     */
    public DataBatch getDataBatch() {
        return dataBatch;
    }

    /**
     * <p>
     * @return return the int value of the Id of the gpu the cpu's currently working on its databatches
     * @PRE: none
     * @POST: none
     */
    public int getGpuID() {
        return gpuID;
    }

    /**
     * <p>
     * @return return the int value of the ticks needed for a single process
     * @PRE: none
     * @POST: none
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * Enter a dataBatch from a gpu to a cpu through the cluster
     * <p>
     * @param dataBatch need to be processed by the cpu
     * @param id the id of the gpu who sent the dataBatch
     * @return return a boolean on whether the CPU can start processing or already in a process
     * @PRE: none
     * @POST: none
     */
    //entering a batch to a cpu, which will make it "processing"
    public synchronized boolean canProcess(DataBatch dataBatch, int id) {
        if (getProcessing())
            return false;
        process(dataBatch, id);
        return true;
    }

    /**
     * An "assist" function that sets the CPU with data to process
     * while setting the ticks amount needed according to the data type
     * <p>
     * @param dataBatch need to be processed by the cpu
     * @param id the id of the gpu who sent the dataBatch
     * @return none
     * @PRE: getGpuID() == null
     *       getDataBatch() == null
     *       getTicks() == 0
     *       getProcessing() == false
     * @POST: getGpuID != null
     *        getDataBatch() != null
     *        getTicks != 0
     *        getProcessing() == true
     */
    public void process(DataBatch dataBatch, int id) {
        this.gpuID = id;
        this.dataBatch = dataBatch;
        Data.Type type = getType();
        switch (type) {
            case Text:
                this.ticks = (32 / cores) * 2;
                break;
            case Images:
                this.ticks = (32 / cores) * 4;
                break;
            case Tabular:
                this.ticks = 32 / cores;
                break;
        }
        processing = true;
    }

    /**
     * This function countdown the ticks amount needed for the processing process to end
     * <p>
     * @return none
     * @PRE: none
     * @POST: none
     */
    public void tick() {
        if (ticks == 0) {
//            System.out.println("lost tick :)");
            return;
        }
        ticks--;
        cluster.getStatistics().setCPU_timeUnitUsed();
        if (ticks == 0) {
            cluster.getStatistics().setCPU_processedDataBatches();
            processing = false;
            cluster.processedDataBatch(dataBatch, gpuID);
            cluster.getNextDataBatch(this);
        }
    }
}