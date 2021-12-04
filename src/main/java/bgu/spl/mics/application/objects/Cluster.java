package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static class ClusterHolder{
		private static Cluster instance = new Cluster();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	private List<GPU> GPUS;
	private List<CPU> CPUS;
	HashMap<Integer, List<DataBatch>> unprocessedDataBatchMap;
	HashMap<Integer, List<DataBatch>> processedDataBatchMap;
	//private Statistics statistics;
	private Cluster(){
		GPUS = new ArrayList<GPU>();
		CPUS = new ArrayList<CPU>();
		unprocessedDataBatchMap = new HashMap<>();
		processedDataBatchMap = new HashMap();
	}

	public void addGPU(GPU gpu)
	{
		GPUS.add(gpu);
		processedDataBatchMap.put(gpu.getId(), new ArrayList<DataBatch>());
	}
	public void addCPU(CPU cpu)
	{
		CPUS.add(cpu);
	}

	//CPU to GPU
	public void processedDataBatch(DataBatch dataBatch, int gpuID) {
		if(!GPUS.get(gpuID).addProcessedDataBatch(dataBatch))
			processedDataBatchMap.get(gpuID).add(dataBatch);
	}
	public void getNextDataBatches(int gpuID) {
		processedDataBatch(processedDataBatchMap.get(gpuID).remove(0), gpuID);
	}
	//GPU to CPU
	public void process(List<DataBatch> unProcessedDataBatch, int gpuID) {
		for(CPU cpu : CPUS)
			if(cpu.canProcess(unProcessedDataBatch.get(0), gpuID))
				unProcessedDataBatch.remove(0);
		unprocessedDataBatchMap.put(gpuID, unProcessedDataBatch);
	}
	public void getNextDataBatch() {
		// priority implements ...
	}



}
