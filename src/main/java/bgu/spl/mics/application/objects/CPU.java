package bgu.spl.mics.application.objects;

import java.awt.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Container data;
    private Cluster cluster;

    public CPU(int cores, Container data, Cluster cluster){
        this.cores = cores;
        this.data = data;
        this.cluster = cluster;
    }

}
