package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private final List<Model> modelList;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        modelList = new ArrayList<>();
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public int getDate() {return date;}
    public void setDate(int date) {this.date = date;}

    public boolean tick(TickBroadcast t){
        date -= 1;
        if(date<=0)
            return true;
        return false;
    }

    public void publish(PublishResultsEvent p) {
        modelList.add(p.getModel());
    }

    public List<Model> getModelList() {
        return modelList;
    }
}
