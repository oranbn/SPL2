package bgu.spl.mics;


import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CPUService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private MessageBusImpl messageBus;
    CPUService cpuS1;
    TrainModel trainModel;
    Model model;
    Data data;
    Data.Type dataType = Data.Type.Images;
    Student student;
    Student.Degree degree;
    Model.Status modelStatus;
    Model.Results results;
    Tick tick;
    @Before
    public void setUp() throws Exception {
        messageBus = MessageBusImpl.getInstance();
        cpuS1 = new CPUService("CPU1");
        data = new Data(dataType, 0, 10000);
        degree = Student.Degree.MSc;
        student = new Student("Moshe", "SE", degree);
        modelStatus = Model.Status.PreTrained;
        results = Model.Results.None;
        model = new Model("model1", data, student,modelStatus, results);
        trainModel = new TrainModel(model);
        tick = new Tick();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        messageBus.subscribeEvent(TrainModel.class, cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModel.class));
        messageBus.register(cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModel.class));
        messageBus.subscribeEvent(TrainModel.class, cpuS1);
        assertTrue(messageBus.isMicroServiceSubscribedEvent(cpuS1, TrainModel.class));
    }

    @Test
    public void subscribeBroadcast() {
        messageBus.subscribeBroadcast(Tick.class, cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, Tick.class));
        messageBus.register(cpuS1);
        assertFalse(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, Tick.class));
        messageBus.subscribeBroadcast(Tick.class, cpuS1);
        assertTrue(messageBus.isMicroServiceSubscribedBroadcast(cpuS1, Tick.class));
    }

    @Test
    public void complete() {

    }

    @Test
    public void sendBroadcast() {
        messageBus.register(cpuS1);
        messageBus.subscribeBroadcast(Tick.class, cpuS1);
        assertFalse(messageBus.isBroadcastEnlistedToMicroService(cpuS1, tick));
        messageBus.sendBroadcast(tick);
        assertTrue(messageBus.isBroadcastEnlistedToMicroService(cpuS1, tick));
    }

    @Test
    public void sendEvent() {
        messageBus.register(cpuS1);
        messageBus.subscribeEvent(TrainModel.class, cpuS1);
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
    }
}