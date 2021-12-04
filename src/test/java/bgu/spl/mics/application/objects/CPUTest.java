package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {
    CPU cpu;
    Data data;
    Data.Type type;
    DataBatch dataBatch;
    int gpuID;
    @Before
    public void setUp() throws Exception {
        cpu = new CPU(4, Cluster.getInstance());
        type = Data.Type.Images;
        data = new Data(type,0,10000);
        dataBatch = new DataBatch(data,0);
        gpuID = 1;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void canProcess() {
        assertTrue(cpu.canProcess(dataBatch,gpuID));
        assertFalse(cpu.canProcess(dataBatch,gpuID));
    }

    @Test
    public void process() {
        cpu.process(dataBatch, gpuID);
        assertEquals(cpu.getGpuID(), gpuID);
        assertEquals(cpu.getDataBatch(), dataBatch);
        assertEquals(cpu.getType(), type);
        assertEquals(cpu.getTicks(), 32);
        assertTrue(cpu.getProcessing());
    }

    @Test
    public void tick() {
        cpu.process(dataBatch,  gpuID);
        assertTrue(cpu.getProcessing());
        for(int i=0;i<32;i++) {
            cpu.tick();
            assertEquals(cpu.getTicks(), 32-1-i);
        }
        assertFalse(cpu.getProcessing());
    }
}