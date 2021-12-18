package bgu.spl.mics;

import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.objects.*;

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
		microServiceBroadcasts = new HashMap<>();
		registerList = new ArrayList<>();
		eventsHashMap.put(TrainModelEvent.class, new ArrayList<>());
		eventsHashMap.put(TestModelEvent.class, new ArrayList<>());
		eventsHashMap.put(PublishResultsEvent.class, new ArrayList<>());
		broadcastHashMap.put(TickBroadcast.class, new ArrayList<>());
		broadcastHashMap.put(PublishConfrenceBroadcast.class, new ArrayList<>());
		broadcastHashMap.put(TerminateBroadcast.class, new ArrayList<>());
		trainModelIterator = new RoundRobinImpl<>(eventsHashMap.get(TrainModelEvent.class)).iterator();
		testModelIterator = new RoundRobinImpl<>(eventsHashMap.get(TestModelEvent.class)).iterator();
		publishResultsIterator = new RoundRobinImpl<>(eventsHashMap.get(PublishResultsEvent.class)).iterator();
	}
	private final HashMap<Class<? extends Event<?>>, List<MicroService>> eventsHashMap;
	private final HashMap<Class<? extends Broadcast>, List<MicroService>> broadcastHashMap;
	private final HashMap<MicroService, List<Event<?>>> microServiceEvents;
	private final HashMap<MicroService, List<Broadcast>> microServiceBroadcasts;
	private final List<MicroService> registerList;
	private final Iterator<MicroService> trainModelIterator;
	private final Iterator<MicroService> testModelIterator;
	private final Iterator<MicroService> publishResultsIterator;

	public boolean isMicroServiceRegistered(MicroService m){return registerList.contains(m);}
	public <T> boolean isMicroServiceSubscribedEvent(MicroService m, Class<? extends Event<T>> type){ return eventsHashMap.get(type).contains(m);}
	public boolean isMicroServiceSubscribedBroadcast(MicroService m, Class<? extends Broadcast> type){ return broadcastHashMap.get(type).contains(m);}
	public boolean isBroadcastEnlistedToMicroService(MicroService m, Broadcast b){ return microServiceBroadcasts.get(m).contains(b); }
	public boolean isEventEnlistedToMicroService(MicroService m, Event e){ return microServiceEvents.get(m).contains(e); }


	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) { //maybe sync it
		// TODO Auto-generated method stub
		if(registerList.contains(m))
			if(!eventsHashMap.get(type).contains(m))
				eventsHashMap.get(type).add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) { //maybe sync it
		// TODO Auto-generated method stub
		if(registerList.contains(m))
			if(!broadcastHashMap.get(type).contains(m))
				broadcastHashMap.get(type).add(m);
	}
	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		e.getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		if(b.getClass()== TickBroadcast.class)
		{
			for(MicroService m :broadcastHashMap.get(TickBroadcast.class))
				if(microServiceBroadcasts.containsKey(m))
				microServiceBroadcasts.get(m).add(b);
		}
		if(b.getClass()== PublishConfrenceBroadcast.class)
		{
			for(MicroService m :broadcastHashMap.get(PublishConfrenceBroadcast.class))
				if(microServiceBroadcasts.containsKey(m))
				microServiceBroadcasts.get(m).add(b);
		}
		if(b.getClass()== TerminateBroadcast.class)
		{
			List<MicroService> microServiceList = broadcastHashMap.get(TerminateBroadcast.class);
			for(MicroService m :microServiceList) {
				System.out.println(m.getName() + " is terminated");
				if (microServiceBroadcasts.containsKey(m))
					microServiceBroadcasts.get(m).add(b);
			}
		}
		synchronized (this) {
			notifyAll();
		}
	}


	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Future<T> future = new Future<>();
		e.setFuture(future);
		if(e.getClass()== TrainModelEvent.class)
			while(true)
			{
				MicroService m = trainModelIterator.next();
				if(microServiceEvents.containsKey(m)) {
					microServiceEvents.get(m).add(e);
					break;
				}
			}
		if(e.getClass()== TestModelEvent.class)
			while(true)
			{
				MicroService m = testModelIterator.next();
				if(microServiceEvents.containsKey(m)) {
					microServiceEvents.get(m).add(e);
					break;
				}
			}
		if(e.getClass()== PublishResultsEvent.class) {
			int count = 0;
			while (true) {
				if(count> CRMSRunner.ConferencesAmount) {
					return null;
				}
				MicroService m = publishResultsIterator.next();
				if (microServiceEvents.containsKey(m)) {
					microServiceEvents.get(m).add(e);
					break;
				}
				count++;
			}
		}
			notifyAll();
		return future;
	}

	@Override
	public synchronized void register(MicroService m) {
		// TODO Auto-generated method stub
		if(!registerList.contains(m)){
			registerList.add(m);
			microServiceEvents.put(m, new ArrayList<Event<?>>());
			microServiceBroadcasts.put(m, new ArrayList<Broadcast>());
		}
	}

	@Override
	public synchronized void unregister(MicroService m) {
		// TODO Auto-generated method stub
		registerList.remove(m);
		microServiceEvents.remove(m);
		microServiceBroadcasts.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		synchronized(this) {
			while(microServiceEvents.get(m).size()==0 && microServiceBroadcasts.get(m).size()==0)
				wait();
		}
		if(microServiceEvents.get(m).size()>0)
			return microServiceEvents.get(m).remove(0);
		return microServiceBroadcasts.get(m).remove(0);
	}
}