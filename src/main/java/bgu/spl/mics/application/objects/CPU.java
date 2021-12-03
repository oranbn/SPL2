package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private DataBatch dataBatch;
    private Cluster cluster;
    private int gpuID; //Oran - to track the gpus
    private int ticks; //Oran - to track the "time" passes
    private boolean processing; //Oran - if in the middle of processing a batcha

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.cluster = cluster;
        this.ticks = 0;
        this.processing = false;
    }
    /**
     * @return return the Data of DataBatch
     * @PRE: none
     * @POST: @return Data
     */
    public Data getData() {
        return dataBatch.getData();
    }
    /**
     * @return return the Type of Data
     * @PRE: none
     * @POST: @return Type
     */
    public Data.Type getType() {
        return getData().getType();
    }
    /**
     * @return return the boolean value of processing
     * @PRE: none
     * @POST: @return processing
     */
    public boolean getProcessing() {
        return processing;
    }

    /**
     * @return return the int value of the Id of the gpu the cpu's currently working on its databatches
     * @PRE: none
     * @POST: @return gpuID
     */
    public int getGpuID() {
        return gpuID;
    }

    /**
     * Enter a dataBatch from a gpu to a cpu through the cluster
     * <p>
     * @return return a boolean on whether the CPU can start processing or already in a process
     * @PRE: none
     * @POST: if processing is false, calls the "process" method,
     *            which sets the CPU with data to process and set processing as true
     *        if processing is true, does nothing
     */
    //entering a batch to a cpu, which will make it "processing"
    public boolean canProcess(DataBatch dataBatch, int id) {
        if (getProcessing())
            return false;
        process(dataBatch, id);
        return true;
    }

    /**
     * An "assist" function that sets the CPU with data to process
     * while setting the ticks amount needed according to the data type
     * <p>
     * @return none
     * @PRE: getProcessing() == false
     *       getDataBatch() == false
     *
     *
     *
     * @POST: getProcessing() == true
     *        
     *
     *
     *
     *
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

    public void tick() {
        if (ticks == 0)
            return;
        ticks--;
        if (ticks == 0) {
            processing = false;
            cluster.processedDataBatch(dataBatch, gpuID);
            cluster.getNextDataBatch();
        }
    }
}