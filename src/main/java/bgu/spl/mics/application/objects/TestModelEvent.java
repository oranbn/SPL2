package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class TestModelEvent implements Event<Model> {
    private Model m;

    @Override
    public Future<Model> getFuture() {
        return future;
    }

    public void setFuture(Future<Model> future) {
        this.future = future;
    }

    private Future<Model> future;
    public TestModelEvent(Model m)
    {
        this.m = m;
    }
    public Model getModel()
    {
        return m;
    }
    public Student getStudent(){return getModel().getStudent();}
}
