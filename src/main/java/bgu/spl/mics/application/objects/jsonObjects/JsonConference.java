package bgu.spl.mics.application.objects.jsonObjects;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.ConferenceService;

import java.util.ArrayList;
import java.util.List;

public class JsonConference {
    private final String name;
    private final int date;
    private final List<JsonModel> publications;

    public JsonConference(String name, int date){
        this.name = name;
        this.date = date;
        this.publications = new ArrayList<>();
    }

    public JsonConference(ConfrenceInformation conference){
        this.name = conference.getName();
        this.date = conference.getDate();
        this.publications = new ArrayList<>();
    }

    public String getName() {return name;}
    public int getDate() {return date;}
    public List<JsonModel> getPublications() {return publications;}
    public void addPublishedModel(JsonModel m){publications.add(m);}
}
