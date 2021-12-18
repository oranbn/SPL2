package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.TerminateBroadcast;
import bgu.spl.mics.application.objects.TickBroadcast;

import java.util.List;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	int tickTime;
	int duration;
	public TimeService(int tickTime, int duration) {
		super("TimeService");
		this.tickTime = tickTime;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		while(duration>0)
		{
			try {
				sendBroadcast(new TickBroadcast(tickTime));
				duration -= 1;
				/*System.out.println(duration);*/
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendBroadcast(new TerminateBroadcast());
	terminate();
	}

}
