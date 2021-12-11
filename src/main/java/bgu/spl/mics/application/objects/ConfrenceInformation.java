package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;

    public List<Model> getModelList() {
        return modelList;
    }

    private List<Model> modelList;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
    }
    public boolean tick(TickBroadcast t){
        date -= t.getTickTime();
        if(date<=0)
            return true;
        return false;
    }

    public void publish(PublishResultsEvent p) {
        modelList.add(p.getModel());
    }

}
