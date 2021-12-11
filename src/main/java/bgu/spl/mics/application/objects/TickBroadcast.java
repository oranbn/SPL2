package bgu.spl.mics.application.objects;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    public int getTickTime() {
        return tickTime;
    }

    private int tickTime;
    public TickBroadcast(int tickTime)
    {
        this.tickTime = tickTime;
    }

}
