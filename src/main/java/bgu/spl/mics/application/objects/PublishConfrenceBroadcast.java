package bgu.spl.mics.application.objects;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;

import java.util.List;

public class PublishConfrenceBroadcast implements Broadcast {

    public List<Model> getModelList() {
        return modelList;
    }

    private List<Model> modelList;
    public PublishConfrenceBroadcast(List<Model> modelList)
    {
        this.modelList = modelList;
    }

}
