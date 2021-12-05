package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageBusHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	public static MessageBusImpl getInstance() {
		return MessageBusImpl.MessageBusHolder.instance;
	}
	private MessageBusImpl(){
		eventsHashMap = new HashMap<>();
		broadcastHashMap = new HashMap<>();
		microServiceEvents = new HashMap<>();
		microServiceBroadCasts = new HashMap<>();
		registerList = new ArrayList<>();
		eventsHashMap.put(TrainModel.class, new ArrayList<>());
		eventsHashMap.put(TestModel.class, new ArrayList<>());
		eventsHashMap.put(PublishResults.class, new ArrayList<>());
		broadcastHashMap.put(Tick.class, new ArrayList<>());
		broadcastHashMap.put(PublishConference.class, new ArrayList<>());
		trainModel = new RoundRobinImpl<MicroService>(eventsHashMap.get(TrainModel.class)).iterator();
		testModel = new RoundRobinImpl<MicroService>(eventsHashMap.get(TestModel.class)).iterator();
		publishResults = new RoundRobinImpl<MicroService>(eventsHashMap.get(PublishResults.class)).iterator();
	}
	private final HashMap<Class<? extends Event<?>>, List<MicroService>> eventsHashMap;
	private final HashMap<Class<? extends Broadcast>, List<MicroService>> broadcastHashMap;
	private final HashMap<MicroService, List<Event<?>>> microServiceEvents;
	private final HashMap<MicroService, List<Broadcast>> microServiceBroadCasts;
	private final List<MicroService> registerList;
	private final Iterator<MicroService> trainModel;
	private final Iterator<MicroService> testModel;
	private final Iterator<MicroService> publishResults;

	public boolean isMicroServiceRegistered(MicroService m){return registerList.contains(m);}
	public <T> boolean isMicroServiceSubscribedEvent(MicroService m, Class<? extends Event<T>> type){ return eventsHashMap.get(type).contains(m);}
	public boolean isMicroServiceSubscribedBroadcast(MicroService m, Class<? extends Broadcast> type){ return broadcastHashMap.get(type).contains(m);}
	public boolean isBroadcastEnlistedToMicroService(MicroService m, Broadcast b){ return microServiceBroadCasts.get(m).contains(b); }
	public boolean isEventEnlistedToMicroService(MicroService m, Event e){ return microServiceEvents.get(m).contains(e); }


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		if(registerList.contains(m))
			if(!eventsHashMap.get(type).contains(m))
				eventsHashMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		if(registerList.contains(m))
			if(!broadcastHashMap.get(type).contains(m))
				broadcastHashMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		e.getStudent().getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		if(b.getClass()==Tick.class)
		{
			for(MicroService m :broadcastHashMap.get(Tick.class))
				microServiceBroadCasts.get(m).add(b);
		}
		if(b.getClass()==PublishConference.class)
		{
			for(MicroService m :broadcastHashMap.get(PublishConference.class))
				microServiceBroadCasts.get(m).add(b);
		}
		//notifyAll();
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Future<T> future = new Future<>();
		if(e.getClass()==TrainModel.class)
			microServiceEvents.get(trainModel.next()).add(e);
		if(e.getClass()==TestModel.class)
			microServiceEvents.get(testModel.next()).add(e);
		if(e.getClass()==PublishResults.class)
			microServiceEvents.get(publishResults.next()).add(e);
		//notifyAll();
		return future;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
		if(!registerList.contains(m)){
			registerList.add(m);
			microServiceEvents.put(m, new ArrayList<Event<?>>());
			microServiceBroadCasts.put(m, new ArrayList<Broadcast>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		registerList.remove(m);
		microServiceEvents.remove(m);
		microServiceBroadCasts.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		while(microServiceEvents.get(m).size()==0 && microServiceBroadCasts.get(m).size()==0)
			wait();
		if(microServiceEvents.get(m).size()>0)
			return microServiceEvents.get(m).remove(0);
		return microServiceBroadCasts.get(m).remove(0);
	}
}