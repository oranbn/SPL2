package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.TerminateBroadcast;
import bgu.spl.mics.application.objects.TickBroadcast;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;
    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast t)-> {
            /*System.out.println(getName() +" doing tick");*/
            cpu.tick();});
        subscribeBroadcast(TerminateBroadcast.class,(t)-> terminate());
    }
}
