package bgu.spl.mics.application.objects;

import java.awt.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private DataBatch dataBatch;
    private Cluster cluster;
    private int id;
    private int ticks;
    private boolean processing;
    public CPU(int cores, Cluster cluster){
        this.cores = cores;
        this.cluster = cluster;
        this.ticks = 0;
        this.processing = false;
    }
    public boolean process(DataBatch dataBatch, int id)
    {
        if(processing)
            return false;
        this.id = id;
        this.dataBatch = dataBatch;
        Data.Type type = dataBatch.getData().getType();
        switch (type) {
            case Text:
                this.ticks = 32/cores*2;
                break;
            case Images:
                this.ticks = 32/cores*4;
                break;
            case Tabular:
                this.ticks = 32/cores;
                break;
        }
        processing = true;
        return true;
    }
    public void tick()
    {
        if(ticks == 0)
            return;
        ticks--;
        if(ticks == 0) {
            this.processing = false;
            cluster.processedData(dataBatch, id);
        }
    }
}
