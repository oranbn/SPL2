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
    private int processedDataBatchesLimit; //limit of queue size

    public GPU(Type type, Cluster cluster, int id) {
        this.type = type;
        this.cluster = cluster;
        this.id = id;
        dataBatchAmount = 0;
        switch (type) {
            case GTX1080:
                ticksNeeded = 4;
                processedDataBatchesLimit = 8;
                break;
            case RTX2080:
                ticksNeeded = 2;
                processedDataBatchesLimit = 16;
                break;
            case RTX3090:
                ticksNeeded = 1;
                processedDataBatchesLimit = 32;
                break;
        }
        currentTicks = ticksNeeded;
        processedDataBatch = new LinkedList<DataBatch>();
    }

    /**
     * @return return the Model's Data
     * @PRE: none
     * @POST: @return data
     */
    public Data getData() {
        return model.getData();
    }

    /**
     * @return return the DataBatch of the class
     * @PRE: none
     * @POST: @return dataBatch
     */
    public int getDataSize() {
        return getData().getSize();
    }

    /**
     * @return return the DataBatch of the class
     * @PRE: none
     * @POST: @return dataBatch
     */
    public int getId() {
        return id;
    }
    public int getDataBatchAmount(){return dataBatchAmount;}
    public int getCurrentTicks(){return currentTicks;}
    public Queue<DataBatch> getProcessedDataBatch(){return processedDataBatch;}
    public Model getModel(){return model;}
    public Model.Status getStatus(){return getModel().getStatus();}
    public Student getStudent(){return model.getStudent();}
    public Student.Degree getDegree(){return getStudent().getDegree();}

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

    private void splitDataToBatches() {
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

    public boolean testModel(Model m) {
        if (model != null)
            return false;
        model = m;
        doTest();
        return true;
    }

    private void doTest() {
        int random = 1 +(int)(Math.random() * 10);
        Model.Results good = Model.Results.Good;
        Model.Results bad = Model.Results.Bad;
        Model.Status tested = Model.Status.Tested;
        if(getDegree() == Student.Degree.MSc)
        {
           if(random == 1)
                model.setResults(good);
           else
               model.setResults(bad);
        }
        else
        {
            if(random==1 || random==2)
                model.setResults(good);
            else
                model.setResults(bad);
        }
        model.setStatus(tested);
        model = null;
    }

    public boolean tick() {
        if (currentTicks == 0 || processedDataBatch.size()==0)
            return false;
        currentTicks--;
        if (currentTicks == 0) {
            processedDataBatch.poll();
            currentTicks = ticksNeeded;
            dataBatchAmount--;
            getData().process();
            if (dataBatchAmount == 0)
                return finishProcess();
            else
                cluster.getNextDataBatches(id);
        }
        return false;
    }

    public boolean addProcessedDataBatches(DataBatch dataBatch)
    {
        if(processedDataBatch.size()==processedDataBatchesLimit)
            return false;
        processedDataBatch.add(dataBatch);
        return true;
    }

    public boolean finishProcess(){
        Model.Status status = Model.Status.Trained;
        model.setStatus(status);
        model = null;
        return true;
}


}
