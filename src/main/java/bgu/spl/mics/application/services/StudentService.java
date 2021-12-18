package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.List;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    public Student getStudent() {
        return student;
    }

    private final Student student;

    public List<Model> getModelList() {
        return modelList;
    }

    private final List<Model> modelList;
    public StudentService(String name, Student student, List<Model> modelList) {
        super(name);
        this.student = student;
        this.modelList = modelList;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast publishConfrenceBroadcast )->{
            List<Model> modelList = publishConfrenceBroadcast.getModelList();
/*
            System.out.println(getName()+" Got publishconferenseBroadCast");
*/
            for(Model m : modelList)
            {
                if(this.student == m.getStudent())
                    student.setPublications(student.getPublications()+1);
                else
                    student.setPapersRead(student.getPapersRead()+1);
/*
                System.out.println(getName()+" publictions: "+student.getPublications() +" read: "+student.getPapersRead());
*/
            }
        });
        subscribeBroadcast(TerminateBroadcast.class,(t)-> terminate());
        Thread thread = new Thread(()-> {
            int i = 0;
/*
            System.out.println(getName()+ " has "+modelList.size() +" Models");
*/
            for(Model m: modelList) {
/*
                System.out.println(getName()+" iteration: "+i++);
*/
                Future<Model> trainModelFuture = sendEvent(new TrainModelEvent(m));
                Model trainedModel = trainModelFuture.get();
/*
                System.out.println(getName()+" iteration: "+i +" passed trainmodel");
*/
                if(trainedModel == null || trainedModel.getStatus() == Model.Status.PreTrained || trainedModel.getStatus() == Model.Status.Training)
                    break;
                if(trainedModel.getStatus() == Model.Status.Trained)
                {
                    Future<Model> testModelFuture = sendEvent(new TestModelEvent(trainedModel));
                    Model testedModel = testModelFuture.get();
/*
                    System.out.println(getName()+" iteration: "+i +" passed testmodel");
*/
                    if(testedModel == null || testedModel.getResults() == Model.Results.None)
                        break;
                    if(testedModel.getResults() == Model.Results.Good)
                    {
                        Future<Model> publishedModelFuture = sendEvent(new PublishResultsEvent(testedModel));
                        /*Model publishedModel = publishedModelFuture.get();*/
                    }
                }
            }
        });
        thread.start();
    }
}