package bgu.spl.mics.application.objects;

import java.util.*;

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
    private int id; //gpu id to pass forward
    private Queue<DataBatch> processedDataBatch;
    private int ticksNeeded;
    private int currentTicks;
    private int dataBatchAmount; //how many batches we got from the data
    private int processedDataBatchesAmount; //limit of queue size

    public GPU(Type type, Cluster cluster, int id) {
        this.type = type;
        this.cluster = cluster;
        this.id = id;
        dataBatchAmount = 0;
        switch (type) {
            case GTX1080:
                ticksNeeded = 4;
                processedDataBatchesAmount = 8;
                break;
            case RTX2080:
                ticksNeeded = 2;
                processedDataBatchesAmount = 16;
                break;
            case RTX3090:
                ticksNeeded = 1;
                processedDataBatchesAmount = 32;
                break;
        }
        currentTicks = ticksNeeded;
        processedDataBatch = new LinkedList<DataBatch>();
    }

    public boolean trainModel(Model m) {
        if (model != null)
            return false;
        model = m;
        Model.Status status = Model.Status.Training;
        model.setStatus(status);
        splitDataToBatches();
        return true;
        //might want to set model as null at the end
    }

    public boolean testModel(Model m) {
        if (model != null)
            return false;
        model = m;
        doTest();
        return true;
    }
    public int getId()
    {
        return id;
    }
    private void doTest() {
        // check if student is phd or something else and do randomize and send it back to the gpu service and he will send it back to the bus)
        // set m.result = good/bad - if phd 0.2 good 0.8 bad / second one is 0.1 good 0.9 bad
        // set model = null;
    }

    public Data getData() {
        return model.getData();
    }

    public int getDataSize() {
        return getData().getSize();
    }

    public void splitDataToBatches() {
        Data data = getData();
        int dataSize = getDataSize();
        List<DataBatch> unProcessedDataBatch = new ArrayList<>();
        int index = 0;
        while (index < dataSize) {
            unProcessedDataBatch.add(new DataBatch(data, index));
            index += 1000;
            dataBatchAmount++;
        }
        cluster.process(unProcessedDataBatch, id);
    }

    public void tick() {
        if (currentTicks == 0)
            return;
        currentTicks--;
        if (currentTicks == 0) {
            processedDataBatch.poll();
            currentTicks = ticksNeeded;
            dataBatchAmount--;
            getData().process();
            if (dataBatchAmount == 0)
                finishProcess();
            else
                cluster.getNextDataBatches(id);
        }
    }

    public boolean addProcessedDataBatches(DataBatch dataBatch)
    {
        if(processedDataBatch.size()==processedDataBatchesAmount)
            return false;
        processedDataBatch.add(dataBatch);
        return true;
    }

    public void finishProcess(){
        Model.Status status = Model.Status.Trained;
        model.setStatus(status);
        model = null;
}


}
