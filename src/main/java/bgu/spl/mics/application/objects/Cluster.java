package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private List<GPU> GPUS;
	private List<CPU> CPUS;
	/*private Statistics statistics;*/
	private static Cluster instance = null;
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if(instance == null) {
			List<GPU> GPUSList = new ArrayList<GPU>();
			List<CPU> CPUSList = new ArrayList<CPU>();
			instance = new Cluster(GPUSList, CPUSList);
		}
		return instance;
	}

	private Cluster(List<GPU> GPUS, List<CPU> CPUS){
		this.GPUS = GPUS;
		this.CPUS = CPUS;
	}
	public void addGPU(GPU gpu)
	{
		GPUS.add(gpu);
	}
	public void addCPU(CPU cpu)
	{
		CPUS.add(cpu);
	}
	public void processedData(DataBatch dataBatch, int id)
	{
		// לחשוב אולי על פיתרון טוב יותר
		while(!GPUS.get(id).processDataBatch(dataBatch))
		{}
	}
	public void unprocessedData(DataBatch dataBatch, int id)
	{

		for(CPU cpu : CPUS)
		{
			if(cpu.process(dataBatch, id))
				break;
		}
	}
}
