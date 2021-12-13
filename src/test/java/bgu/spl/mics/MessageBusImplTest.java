package bgu.spl.mics;


import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private MessageBusImpl messageBus;
    CPUService cpuS1;
    GPUService gpuS1;
    TrainModelEvent trainModel;
    Model model;
    Data data;
    Data.Type dataType = Data.Type.Images;
    Student student;
    Student.Degree degree;
    Model.Status modelStatus;
    Model.Results results;
    TickBroadcast tick;
    @Before
    public void setUp() throws Exception {
        messageBus = MessageBusImpl.getInstance();
        cpuS1 = new CPUService("CPU1", new CPU(32, Cluster.getInstance()));
        gpuS1 = new GPUService("GPU1", new GPU(GPU.Type.RTX3090, Cluster.getInstance(),0, ""));
        data = new Data(dataType, 0, 10000);
        degree = Student.Degree.MSc;
        student = new Student("Moshe", "SE", degree);
        modelStatus = Model.Status.PreTrained;
        results = Model.Results.None;
        model = new Model("model1", data, student,modelStatus, results);
        trainModel = new TrainModelEvent(model);
        tick = new TickBroadcast(50);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        messageBus.subscribeEvent(TrainModelEvent.class, cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModelEvent.class));
        messageBus.register(cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModelEvent.class));
        messageBus.subscribeEvent(TrainModelEvent.class, cpuS1);
        assertTrue(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModelEvent.class));
    }

    @Test
    public void subscribeBroadcast() {
        messageBus.subscribeBroadcast(TickBroadcast.class, cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, TickBroadcast.class));
        messageBus.register(cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, TickBroadcast.class));
        messageBus.subscribeBroadcast(TickBroadcast.class, cpuS1);
        assertTrue(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, TickBroadcast.class));
    }

    @Test
    public void complete() {
        messageBus.register(gpuS1);
        messageBus.subscribeEvent(TrainModelEvent.class, gpuS1);
        Future<Model> f = messageBus.sendEvent(trainModel);
        trainModel.setFuture(f);
        assertFalse(f.isDone());
        messageBus.complete(trainModel, model);
        assertTrue(f.isDone());
    }

    @Test
    public void sendBroadcast() {
        messageBus.register(cpuS1);
        messageBus.subscribeBroadcast(TickBroadcast.class, cpuS1);
        assertFalse(messageBus.isBroadcastEnlistedToMicroService(cpuS1, tick));
        messageBus.sendBroadcast(tick);
        assertTrue(messageBus.isBroadcastEnlistedToMicroService(cpuS1, tick));
    }

    @Test
    public void sendEvent() {
        messageBus.register(cpuS1);
        messageBus.subscribeEvent(TrainModelEvent.class, cpuS1);
        assertFalse(messageBus.isEventEnlistedToMicroService(cpuS1, trainModel));
        messageBus.sendEvent(trainModel);
        assertTrue(messageBus.isEventEnlistedToMicroService(cpuS1, trainModel));
    }

    @Test
    public void register() {
        assertFalse(messageBus.isMicroServiceRegistered(cpuS1));
        messageBus.register(cpuS1);
        assertTrue(messageBus.isMicroServiceRegistered(cpuS1));
    }

    @Test
    public void unregister() {
        messageBus.register(cpuS1);
        assertTrue(messageBus.isMicroServiceRegistered(cpuS1));
        messageBus.unregister(cpuS1);
        assertFalse(messageBus.isMicroServiceRegistered(cpuS1));
    }

    @Test
    public void awaitMessage() {
        messageBus.register(cpuS1);
        messageBus.subscribeBroadcast(TickBroadcast.class, cpuS1);
        Thread t = new Thread(()->
        {
            try{
                Message message = messageBus.awaitMessage(cpuS1);
                assertEquals(message, tick);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                fail();
            }
        });
        t.start();
        messageBus.sendBroadcast(tick);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
    }
}