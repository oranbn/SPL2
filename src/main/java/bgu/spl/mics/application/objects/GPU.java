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

    public boolean isTraining() {
        return training;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private boolean training;
    private int id; //gpu id to pass forward
    private Queue<DataBatch> processedDataBatchQueue;
    private int ticksNeeded;
    private int currentTicks;
    private int dataBatchAmount; //how many batches we got from the data
    private int processedDataBatchesLimit; //limit of queue size
    private String name;
    private int trainCounter;
    public GPU(Type type, Cluster cluster, int id, String name) {
        this.type = type;
        this.cluster = cluster;
        this.id = id;
        this.name = name;
        this.trainCounter = 0;
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
        currentTicks = 0;
        training = false;
        processedDataBatchQueue = new LinkedList<DataBatch>();
        Cluster.getInstance().addGPU(this);
    }
    public String getName(){return name;}

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
     * @return returns the ID of the gpu
     * @PRE: none
     * @POST: none
     */
    public int getId() {
        return id;
    }

    /**
     * <p>
     * @return returns the amount of DataBatches in the gpu
     * @PRE: none
     * @POST: none
     */
    public int getDataBatchAmount(){return dataBatchAmount;}

    /**
     * <p>
     * @return returns the ticks left currently to finish a process
     * @PRE: none
     * @POST: none
     */
    public int getCurrentTicks(){return currentTicks;}

    /**
     * <p>
     * @return returns the queue containing all dataBatches which were already processed by the CPU
     * @PRE: none
     * @POST: none
     */
    public Queue<DataBatch> getProcessedDataBatch(){return processedDataBatchQueue;}

    /**
     * <p>
     * @return returns the Status(trained\tested\...) of the model
     * @PRE: none
     * @POST: none
     */
    public Model.Status getModelStatus(){return getModel().getStatus();}

    /**
     * <p>
     * @return returns the Results(Good\Bad) of the model
     * @PRE: none
     * @POST: none
     */
    public Model.Results getModelResults(){return getModel().getResults();}

    /**
     * <p>
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
     * @param m the model that start the training process
     * @return returns a boolean on whether the gpu can start the model's training process or already works on another model
     * @PRE: none
     * @POST: none
     */
    public boolean trainModel(Model m) {
        if (training) {
//            System.out.println(name + " Denied in trainModel and the model is: "+m.getName());
            return false;
        }
//        System.out.println(name + " Accepted in trainModel to the model: "+m.getName());
        model = m;
        trainCounter++;
//        System.out.println(name+ " train counter is: "+trainCounter);
        Model.Status status = Model.Status.Training;
        currentTicks = ticksNeeded;
        training = true;
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
     * @POST: getDataBatchAmount() > 0
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
        cluster.process(unProcessedDataBatch, id, type);
    }

    /**
     * This function receives a Model and randomly choosing whether the model results are good or bad.
     * Then changes the model's status to tested and clears the gpu to be able to receive another model
     * <p>
     * @param m the model that start the testing process
     * @return returns a boolean on whether the gpu can start train the model or already works on another model
     * @PRE: none
     * @POST: none
     */
    public boolean testModel(Model m) {
        if (training) {
//            System.out.println("Denied in testModel " + name);
            return false;
        }
//        System.out.println("Accepted in testModel " + name);
        model = m;
        doTest();
        return true;
    }

    /**
     * An "assist" function that is called from the testModel method, generates a natural random number out of 10
     * and sets the model result by predetermined chance, and sets the current model as tested
     * <p>
     * @return none
     * @PRE: getModel() == null
     *       getModelResults() == null
     *       getModelStatus() == Trained
     *
     * @POST: getModel() == null
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
           if(random <= 6)
                model.setResults(good);
           else
               model.setResults(bad);
        }
        else
        {
            if(random<=8)
                model.setResults(good);
            else
                model.setResults(bad);
        }
        model.setStatus(tested);
        System.out.println("Finished testModel: " + model.getName() + " by: " + name);
        model = null;
    }

    /**
     * This function countdown the ticks amount needed to finish processing the dataBatch
     * <p>
     * @return true if enough ticks have been made to finish training a processed dataBatch, false otherwise
     * @PRE: none
     * @POST: none
     */
    public boolean tick() {
/*
        System.out.println("GPU: " + getName() + " Got TickRequest, his currentstick: "+currentTicks+"  processedDatabatch size is: "+processedDataBatchQueue.size());
*/
        if (currentTicks == 0 || processedDataBatchQueue.size()==0)
            return false;
/*
        System.out.println("GPU: " + getName() + " Passed first condition");
*/
        currentTicks--;
        cluster.getStatistics().setGPU_timeUnitUsed();
        if (currentTicks == 0) {
            processedDataBatchQueue.poll();
            currentTicks = ticksNeeded;
            dataBatchAmount--;
/*
            System.out.println("GPU: " + getName() + "; data batch amount left:"+dataBatchAmount);
*/
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
     * @return none
     * @PRE: getStatus() == Training
     *       getModel() != null
     * @POST: getStatus() == Trained
     *        getModel() == null
     */
    public void finishTrain(){
        Model.Status status = Model.Status.Trained;
        model.setStatus(status);
        training = false;
        currentTicks = 0;
        System.out.println("Finished trainModel: " + model.getName() + " by: " + name);
        model = null;
    }

    /**
     * This function tries to add a processed data batch to the GPU processed Queue, if the list limit
     * have been reached, return false.
     * <p>
     * @param dataBatch!=null the databatch who have been processed in cpu and will be processed in gpu
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
