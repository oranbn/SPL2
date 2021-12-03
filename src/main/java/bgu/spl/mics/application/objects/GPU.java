package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private int id;
    private List<DataBatch> unProcessedDataBatch;
    private List<DataBatch> processedDataBatch;
    int ticksNeeded;
    int ticks;
    int dataBatchAmount;
    int processedDataBatchesAmount;
    public GPU(Type type, Cluster cluster, int id){
        this.type = type;
        this.cluster = cluster;
        this.id = id;
        dataBatchAmount=0;
        switch(type){
            case GTX1080:
                ticksNeeded=4;
                processedDataBatchesAmount = 8;
                break;
            case RTX2080:
                ticksNeeded=2;
                processedDataBatchesAmount = 16;
                break;
            case RTX3090:
                ticksNeeded=1;
                processedDataBatchesAmount = 32;
                break;
        }
        ticks = ticksNeeded;
        unProcessedDataBatch = new ArrayList<DataBatch>();
        processedDataBatch = new ArrayList<DataBatch>();
        makeDataBatch();
    }
    public void trainModel(Model m)
    {
        while(model!=null)
        {}
        model = m;
    }
    public Data getData()
    {
        return model.getData();
    }
    public void makeDataBatch()
    {
        Data data = getData();
        int size = data.getSize();
        int index=0;
        while(size>0)
        {
            size -= 1000;
            unProcessedDataBatch.add(new DataBatch(data,index));
            index += 1000;
            dataBatchAmount++;
        }
    }
    public void sendUnProcessedDataBatch()
    {
        if(unProcessedDataBatch.size()>0)
        cluster.unprocessedData(unProcessedDataBatch.remove(0), id);
    }
    public void tick()
    {
        if(processedDataBatch.size()==0)
            return;
        ticks--;
        if(ticks==0)
        {
            processedDataBatch.remove(0);
            ticks = ticksNeeded;
            dataBatchAmount--;
            getData().process();
            if(dataBatchAmount==0)
                finishProcess();
        }
    }
    public boolean processDataBatch(DataBatch dataBatch)
    {
        if(processedDataBatch.size()==processedDataBatchesAmount)
            return false; //
        processedDataBatch.add(dataBatch);
        return true;
    }
    private void finishProcess()
    {
        // finish process return a result or something...
    }
}
