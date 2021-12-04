package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 * @INV: processedDataBatchQueue.size() >= 0
 *       ticksNeeded >= 0
 *       currentTicks >= 0
 *       dataBatchAmount >= 0
 *       processedDataBatchesLimit >= 0
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
    private Queue<DataBatch> processedDataBatchQueue;
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
        processedDataBatchQueue = new LinkedList<DataBatch>();
    }

    /**
     * <p>
     * @param
     * @return returns the Model which the gpu is currently working on its dataBatches
     * @PRE: none
     * @POST: none
     */
    public Model getModel(){return model;}

    /**
     * <p>
     * @param
     * @return returns the model's Data
     * @PRE: none
     * @POST: none
     */
    public Data getModelData() {
        return getModel().getData();
    }

    /**
     * <p>
     * @param
     * @return returns the model's data's size
     * @PRE: none
     * @POST: none
     */
    public int getModelDataSize() {
        return getModelData().getSize();
    }

    /**
     * <p>
     * @param
     * @return returns the ID of the gpu
     * @PRE: none
     * @POST: none
     */
    public int getId() {
        return id;
    }

    /**
     * <p>
     * @param
     * @return returns the amount of DataBatches in the gpu
     * @PRE: none
     * @POST: none
     */
    public int getDataBatchAmount(){return dataBatchAmount;}

    /**
     * <p>
     * @param
     * @return returns the ticks left currently to finish a process
     * @PRE: none
     * @POST: none
     */
    public int getCurrentTicks(){return currentTicks;}

    /**
     * <p>
     * @param
     * @return returns the queue containing all dataBatches which were arlready processed by the CPU
     * @PRE: none
     * @POST: none
     */
    public Queue<DataBatch> getProcessedDataBatch(){return processedDataBatchQueue;}

    /**
     * <p>
     * @param
     * @return returns the Status(trained\tested\...) of the model
     * @PRE: none
     * @POST: none
     */
    public Model.Status getModelStatus(){return getModel().getStatus();}

    /**
     * <p>
     * @param
     * @return returns the Results(Good\Bad) of the model
     * @PRE: none
     * @POST: none
     */
    public Model.Results getModelResults(){return getModel().getResults();}

    /**
     * <p>
     * @param
     * @return returns the Student that the model is his
     * @PRE: none
     * @POST: none
     */
    public Student getModelStudent(){return model.getStudent();}

    /**
     * <p>
     * @param
     * @return returns the Degree of the Student that the Model is his
     * @PRE: none
     * @POST: none
     */
    public Student.Degree getModelStudentDegree(){return getModelStudent().getDegree();}

    /**
     * This function receives a Model and split its data to batches,
     * then sends it to the cluster to start processing.
     * <p>
     * @param
     * @return returns a boolean on whether the gpu can start the model's training process or already works on another model
     * @PRE: none
     * @POST: none
     */
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

    /**
     * An "assist" function that is called from trainModel, split the model's data into batches
     * and send it to the cluster to process
     * <p>
     * @return none
     * @PRE: getModel() != null
     *       getModelStatus() == Training
     *
     * @POST: getDataBatchAmount() > 0 (can we check empty data models?) //ORAN ORAN ORAN ORAN ORAN ORAN//
     */
    private void splitDataToBatches() {
        Data data = getModelData();
        int dataSize = getModelDataSize();
        List<DataBatch> unProcessedDataBatch = new ArrayList<>();
        int index = 0;
        while (index < dataSize) {
            unProcessedDataBatch.add(new DataBatch(data, index));
            index += 1000;
            dataBatchAmount++;
        }
        cluster.process(unProcessedDataBatch, id);
        model = null;                                                            //(is it okay?)   //ORAN ORAN ORAN ORAN ORAN ORAN//
    }

    /**
     * This function receives a Model and randomly choosing whether the model results are good or bad.
     * Then changes the model's status to tested and clears the gpu to be able to receive another model
     * <p>
     * @param
     * @return returns a boolean on whether the gpu can start train the model or already works on another model
     * @PRE: none
     * @POST: none
     */
    public boolean testModel(Model m) {
        if (model != null)
            return false;
        model = m;
        doTest();
        return true;
    }

    /**
     * An "assist" function that is called from the testModel method, generates a natural random number out of 10
     * and sets the model result by predetermined chance, and sets the current model as tested
     * <p>
     * @param
     * @return none
     * @PRE: getModel() == null
     *       getModelResults() == null
     *       getModelStatus() == Trained (if it's getting tested does it have to be trained?) //ORAN ORAN ORAN ORAN ORAN ORAN//
     *
     * @POST: getModel() == null (is it null at the end or what?) //ORAN ORAN ORAN ORAN ORAN ORAN//
     *        getModelResults() != null
     *        getModelStatus() == Tested
     */
    private void doTest() {
        int random = 1 +(int)(Math.random() * 10);
        Model.Results good = Model.Results.Good;
        Model.Results bad = Model.Results.Bad;
        Model.Status tested = Model.Status.Tested;
        if(getModelStudentDegree() == Student.Degree.MSc)
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

    /**
     * This function countdown the ticks amount needed to finish processing the dataBatch
     * <p>
     * @param
     * @return true if enough ticks have been made to finish training a processed dataBatch, false otherwise
     * @PRE: none
     * @POST: none
     */
    public boolean tick() {
        if (currentTicks == 0 || processedDataBatchQueue.size()==0)
            return false;
        currentTicks--;
        if (currentTicks == 0) {
            processedDataBatchQueue.poll();
            currentTicks = ticksNeeded;
            dataBatchAmount--;
            getModelData().process();
            if (dataBatchAmount == 0){
                finishTrain();
                return true;
            }
            else
                cluster.getNextDataBatches(id);
        }
        return false;
    }

    /**
     * This function is called if enough ticks have been made to finish training the model.
     * It sets the model status to trained and frees the GPU to handle a new event
     * <p>
     * @param
     * @return none
     * @PRE: getStatus() == Training
     *       getModel() != null
     * @POST: getStatus() == Trained
     *        getModel() == null
     */
    public void finishTrain(){
        Model.Status status = Model.Status.Trained;
        model.setStatus(status);
        model = null;
        //return true;
    }

    /**
     * This function tries to add a processed data batch to the GPU processed Queue, if the list limit
     * have been reached, return false.
     * <p>
     * @param
     * @return none
     * @PRE: none
     * @POST: none
     */
    public boolean addProcessedDataBatch(DataBatch dataBatch)
    {
        if(processedDataBatchQueue.size()==processedDataBatchesLimit)
            return false;
        processedDataBatchQueue.add(dataBatch);
        return true;
    }


}
