package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;

public class PublishResults implements Event<Model> {
    private Model m;

    public PublishResults(Model m)
    {
        this.m = m;
    }
    public Model getModel()
    {
        return m;
    }
    public Student getStudent(){return getModel().getStudent();}
}
