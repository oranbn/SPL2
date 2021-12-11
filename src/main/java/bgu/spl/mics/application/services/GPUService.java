package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;
import java.util.List;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private List<Event<Model>> events;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        events = new LinkedList<>();
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class,(TrainModelEvent t)-> {
            events.add(t);
            if(events.get(0).getClass() == TrainModelEvent.class)
                gpu.trainModel(events.get(0).getModel());
            else {
                gpu.testModel(events.get(0).getModel());
                complete(events.get(0), events.get(0).getModel());
                events.remove(0);
            }
        });
        subscribeEvent(TestModelEvent.class,(TestModelEvent t)->{
            events.add(t);
            if(events.get(0).getClass() == TrainModelEvent.class)
                gpu.trainModel(events.get(0).getModel());
            else {
                gpu.testModel(events.get(0).getModel());
                complete(events.get(0), events.get(0).getModel());
                events.remove(0);
            }
        });
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast t)-> {
            if(gpu.tick()) {
                complete(events.get(0), events.get(0).getModel());
                events.remove(0);
                if(events.size()>0)
                if (events.get(0).getClass() == TrainModelEvent.class)
                    gpu.trainModel(events.get(0).getModel());
                else
                    gpu.testModel(events.get(0).getModel());
            }
        });

    }
}
