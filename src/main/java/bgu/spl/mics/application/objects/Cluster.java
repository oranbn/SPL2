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
		private final static Cluster instance = new Cluster();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	private final List<GPU> GPUS;
	private final List<CPU> CPUS;
	int roundRobin3090;
	int roundRobin2080;
	int roundRobin1080;
	HashMap<Integer, List<DataBatch>> unprocessedDataBatchMapGPU_RTX3090;
	HashMap<Integer, List<DataBatch>> unprocessedDataBatchMapGPU_RTX2080;
	HashMap<Integer, List<DataBatch>> unprocessedDataBatchMapGPU_GTX1080;
	HashMap<Integer, List<DataBatch>> processedDataBatchMap;

	public Statistics getStatistics() {
		return statistics;
	}

	private final Statistics statistics;
	private Cluster(){
		GPUS = new ArrayList<GPU>();
		CPUS = new ArrayList<CPU>();
		unprocessedDataBatchMapGPU_RTX3090 = new HashMap<>();
		unprocessedDataBatchMapGPU_RTX2080 = new HashMap<>();
		unprocessedDataBatchMapGPU_GTX1080 = new HashMap<>();
		processedDataBatchMap = new HashMap();
		roundRobin3090 = 0;
		roundRobin2080 = 0;
		roundRobin1080 = 0;
		statistics = new Statistics();
	}

	public synchronized void addGPU(GPU gpu)
	{
		GPUS.add(gpu);
		processedDataBatchMap.put(gpu.getId(), new ArrayList<DataBatch>());
	}
	public synchronized void addCPU(CPU cpu)
	{
		CPUS.add(cpu);
	}

	//CPU to GPU
	public synchronized void processedDataBatch(DataBatch dataBatch, int gpuID) {
		if(!GPUS.get(gpuID).addProcessedDataBatch(dataBatch))
			processedDataBatchMap.get(gpuID).add(dataBatch);
	}
	public synchronized void getNextDataBatches(int gpuID) {
		if(processedDataBatchMap.get(gpuID).size()>0)
			processedDataBatch(processedDataBatchMap.get(gpuID).remove(0), gpuID);
	}
	public HashMap<Integer, List<DataBatch>> getProcessedDataBatchMap(){return processedDataBatchMap;}
	//GPU to CPU
	public synchronized void process(List<DataBatch> unProcessedDataBatch, int gpuID, GPU.Type type) {
		for(CPU cpu : CPUS) {
			if (unProcessedDataBatch.size() == 0)
				return;
			if (cpu.canProcess(unProcessedDataBatch.get(0), gpuID))
				unProcessedDataBatch.remove(0);
		}
		if(type == GPU.Type.RTX3090) {
			unprocessedDataBatchMapGPU_RTX3090.remove(gpuID);
			unprocessedDataBatchMapGPU_RTX3090.put(gpuID, unProcessedDataBatch);
		}
		if(type == GPU.Type.RTX2080) {
			unprocessedDataBatchMapGPU_RTX2080.remove(gpuID);
			unprocessedDataBatchMapGPU_RTX2080.put(gpuID, unProcessedDataBatch);
		}
		if(type == GPU.Type.GTX1080) {
			unprocessedDataBatchMapGPU_GTX1080.remove(gpuID);
			unprocessedDataBatchMapGPU_GTX1080.put(gpuID, unProcessedDataBatch);
		}
	}

	public synchronized void getNextDataBatch(CPU cpu) {
		int count = unprocessedDataBatchMapGPU_RTX3090.size()*4 + unprocessedDataBatchMapGPU_RTX2080.size()*2 + unprocessedDataBatchMapGPU_GTX1080.size() ;
		while(true) {
			if(count<0)
				break;
			count--;
			if (roundRobin3090 < unprocessedDataBatchMapGPU_RTX3090.size()) {
				int key = (int) unprocessedDataBatchMapGPU_RTX3090.keySet().toArray()[roundRobin3090];
				if (unprocessedDataBatchMapGPU_RTX3090.get(key).size() > 0) {
					if(cpu.canProcess(unprocessedDataBatchMapGPU_RTX3090.get(key).get(0), key))
						unprocessedDataBatchMapGPU_RTX3090.get(key).remove(0);
					roundRobin3090++;
					break;
				}
				else
					roundRobin3090++;
			}
			else if(roundRobin2080 < unprocessedDataBatchMapGPU_RTX2080.size()) {
				int key = (int) unprocessedDataBatchMapGPU_RTX2080.keySet().toArray()[roundRobin2080];
				if (unprocessedDataBatchMapGPU_RTX2080.get(key).size() > 0) {
					if(cpu.canProcess(unprocessedDataBatchMapGPU_RTX2080.get(key).get(0), key))
						unprocessedDataBatchMapGPU_RTX2080.get(key).remove(0);
					roundRobin2080++;
					roundRobin3090 = 0;
					break;
				}
				else
					roundRobin2080++;
			}
			else if(roundRobin1080 < unprocessedDataBatchMapGPU_GTX1080.size()) {
				int key = (int) unprocessedDataBatchMapGPU_GTX1080.keySet().toArray()[roundRobin1080];
				if (unprocessedDataBatchMapGPU_GTX1080.get(key).size() > 0) {
					if(cpu.canProcess(unprocessedDataBatchMapGPU_GTX1080.get(key).get(0), key))
						unprocessedDataBatchMapGPU_GTX1080.get(key).remove(0);
					roundRobin1080++;
					roundRobin3090 = 0;
					roundRobin2080 = 0;
					break;
				}
				else
					roundRobin1080++;
			}
			else
			{
				roundRobin2080=0;
				roundRobin3090=0;
				roundRobin1080=0;
			}
		}
	}



}
