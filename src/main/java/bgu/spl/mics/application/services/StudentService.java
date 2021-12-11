package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.PublishConfrenceBroadcast;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.TrainModelEvent;
import bgu.spl.mics.application.objects.TestModelEvent;
import bgu.spl.mics.application.objects.PublishResultsEvent;


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
            for(Model m : modelList)
            {
                if(this.student == m.getStudent())
                    student.setPublications(student.getPublications()+1);
                else
                    student.setPapersRead(student.getPapersRead()+1);
            }
        });
        for(Model m: modelList) {
            Future<Model> trainModelFuture = sendEvent(new TrainModelEvent(m));
            Model trainedModel = trainModelFuture.get();
            if(trainedModel != null && trainedModel.getStatus() == Model.Status.Trained)
            {
                Future<Model> testModelFuture = sendEvent(new TestModelEvent(trainedModel));
                Model testedModel = testModelFuture.get();
                if(testedModel != null && testedModel.getResults() == Model.Results.Good)
                {
                    Future<Model> publishedModelFuture = sendEvent(new PublishResultsEvent(testedModel));
                    Model publishedModel = publishedModelFuture.get();
                }
            }
        }
    }
}
// po ani rotze send event:
// Future<Model> f = sendEvent(trainModel);
// Model m = f.get();
// m.getStatus() == trained
// Future<Model> f2 = sendEvent(testModel);
// Model m2 = f2.get();
// m2.getResults() == good
// Future<Model> f3 = sendEvent(publishREsults)
// student.setPublictions(student.getpublictions+1)
// else
// start loop again