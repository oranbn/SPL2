package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;

    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
    }
}
