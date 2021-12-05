package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;

public class TrainModel implements Event<Model> {
    private Model m;

    public TrainModel(Model m)
    {
        this.m = m;
    }
    public Model getModel()
    {
        return m;
    }
    public Student getStudent(){return getModel().getStudent();}
}
