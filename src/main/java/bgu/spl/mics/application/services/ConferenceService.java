package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private final ConfrenceInformation confrenceInformation;

    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        this.confrenceInformation = confrenceInformation;
        // TODO Implement this
    }

    public ConfrenceInformation getConfrenceInformation() {return confrenceInformation;}

    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent p)->{
            confrenceInformation.publish(p);
            complete(p, p.getModel());
        });
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast t)->{if(confrenceInformation.tick(t)){terminate();
            sendBroadcast(new PublishConfrenceBroadcast(confrenceInformation.getModelList()));
/*
            System.out.println("Model Amount: "+confrenceInformation.getModelList().size());
*/
            }});
        subscribeBroadcast(TerminateBroadcast.class,(t)-> terminate());
    }
}
