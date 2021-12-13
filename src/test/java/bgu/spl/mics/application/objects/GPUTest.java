package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    Cluster cluster;
    GPU.Type type;
    int id;
    GPU gpu;
    Model model;
    Data data;
    Data.Type dataType = Data.Type.Images;
    Student student;
    Student.Degree degree;
    Model.Status modelStatus;
    Model.Results results;
    @Before
    public void setUp() throws Exception {
        type = GPU.Type.RTX2080;
        cluster = Cluster.getInstance();
        id = 0;
        gpu = new GPU(type,cluster,id, "");
        data = new Data(dataType, 0, 10000);
        degree = Student.Degree.MSc;
        student = new Student("Moshe", "SE", degree);
        modelStatus = Model.Status.PreTrained;
        results = Model.Results.None;
        model = new Model("model1", data, student,modelStatus, results);
        cluster.addGPU(gpu);
        cluster.getProcessedDataBatchMap().get(0).add(new DataBatch(data, 0));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void trainModel() {
        assertTrue(gpu.trainModel(model));
        assertFalse(gpu.trainModel(model));
        Model.Status status = Model.Status.Training;
        assertEquals(gpu.getModelStatus(), status);
        assertEquals(gpu.getModelData(), data);
        assertEquals(gpu.getDataBatchAmount(), 10);
    }

    @Test
    public void testModel() {
        assertTrue(gpu.testModel(model));
    }

    @Test
    public void tick() {
        gpu.trainModel(model);
        gpu.addProcessedDataBatch(new DataBatch(data, 0));
        assertEquals(gpu.getCurrentTicks(), 2);
        assertEquals(gpu.getDataBatchAmount(), 10);
        assertEquals(gpu.getProcessedDataBatch().size(), 1);
        gpu.tick();
        assertEquals(gpu.getCurrentTicks(), 1);
        assertEquals(gpu.getDataBatchAmount(), 10);
        assertEquals(gpu.getProcessedDataBatch().size(), 1);
        gpu.tick();
        assertEquals(gpu.getCurrentTicks(), 2);
        assertEquals(gpu.getDataBatchAmount(),9);
        assertEquals(gpu.getProcessedDataBatch().size(), 0);
    }

    @Test
    public void addProcessedDataBatches() {
        assertEquals(gpu.getProcessedDataBatch().size(), 0);
        for(int i = 0; i < 16; i++) {
            assertTrue(gpu.addProcessedDataBatch(new DataBatch(data, 0)));
            assertEquals(gpu.getProcessedDataBatch().size(), i+1);
        }
        assertFalse(gpu.addProcessedDataBatch(new DataBatch(data, 0)));
        assertEquals(gpu.getProcessedDataBatch().size(), 16);
    }

    @Test
    public void finishProcess() {
        gpu.trainModel(model);
        Model.Status training = Model.Status.Training;
        assertEquals(gpu.getModelStatus(), training);
    }
}