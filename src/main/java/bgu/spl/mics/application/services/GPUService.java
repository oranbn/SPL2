package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;
import java.util.List;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private final GPU gpu;
    private final List<Event<Model>> trainModelEvent;
    private final List<Event<Model>> testModelEvent;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        trainModelEvent = new LinkedList<>();
        testModelEvent = new LinkedList<>();
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class,(TrainModelEvent t)-> {
            trainModelEvent.add(t);
/*
            System.out.println(getName() + " received trainModelEvent: "+trainModelEvent.get(trainModelEvent.size()-1).getModel().getName()+" and the amount of the queued events WITHHIM are " +trainModelEvent.size());
*/
            while(testModelEvent.size()>0 && gpu.testModel(testModelEvent.get(0).getModel())) {
                complete(testModelEvent.get(0), testModelEvent.get(0).getModel());
                testModelEvent.remove(0);
            }
            gpu.trainModel(trainModelEvent.get(0).getModel());
        });
        subscribeEvent(TestModelEvent.class,(TestModelEvent t)->{
            testModelEvent.add(t);
/*
            System.out.println(getName() + " received testModelEvent: "+testModelEvent.get(testModelEvent.size()-1).getModel().getName()+" and the amount of the queued events WITHHIM are " +testModelEvent.size());
*/
            while(testModelEvent.size()>0 && gpu.testModel(testModelEvent.get(0).getModel())) {
                complete(testModelEvent.get(0), testModelEvent.get(0).getModel());
                testModelEvent.remove(0);
            }
            if(trainModelEvent.size()>0)
                gpu.trainModel(trainModelEvent.get(0).getModel());
        });
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast t)-> {
/*
            System.out.println("Yoav Test2: "+getName() +" trainmodelevent size: "+trainModelEvent.size()+" testmodeleventsize: "+testModelEvent.size());
*/
            if(gpu.tick()) {
/*
                System.out.println(getName() + " got tick, which says we finished training the model: " + trainModelEvent.get(0).getModel().getName());
*/
                complete(trainModelEvent.get(0), trainModelEvent.get(0).getModel());
                trainModelEvent.remove(0);
/*
                System.out.println("Yoav Test: "+getName() +" tranmodelevent size: "+trainModelEvent.size()+" testmodeleventsize: "+testModelEvent.size());
*/              if(trainModelEvent.size()>0)
/*
                    System.out.println(getName() + " is now trying to train: " + trainModelEvent.get(0).getModel().getName());
*/
                while(testModelEvent.size()>0 && gpu.testModel(testModelEvent.get(0).getModel())){
/*
                    System.out.println(getName() + " finished testing model: " + testModelEvent.get(0).getModel().getName() + "; and now got left in testModelQueue: " + (testModelEvent.size()-1));
*/
                    complete(testModelEvent.get(0), testModelEvent.get(0).getModel());
                    testModelEvent.remove(0);
                }
                if(trainModelEvent.size()>0) {
/*
                    System.out.println(getName() + " is trying to start training model: " + trainModelEvent.get(0).getModel().getName() + "; and now got left in trainModelQueue: " + (trainModelEvent.size()));
*/
                    gpu.trainModel(trainModelEvent.get(0).getModel());
                }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class,(t)-> {
            while(trainModelEvent.size()>0) {
                complete(trainModelEvent.get(0), trainModelEvent.get(0).getModel());
                trainModelEvent.remove(0);
            }
            while(testModelEvent.size()>0) {
                complete(testModelEvent.get(0), testModelEvent.get(0).getModel());
                testModelEvent.remove(0);
            }
            terminate();});
    }
}
