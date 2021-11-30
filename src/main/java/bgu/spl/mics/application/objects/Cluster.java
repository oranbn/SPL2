package bgu.spl.mics.application.objects;


import java.util.Collection;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private Collection<GPU> GPUS;
	private Collection<CPU> CPUS;
	private Statistics statistics;
	private static Cluster instance = null;
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance(Collection<GPU> GPUS, Collection<CPU> CPUS, Statistics statistics) {
		if(instance == null)
			instance = new Cluster(GPUS, CPUS, statistics);
		return instance;

	}

	private Cluster(Collection<GPU> GPUS, Collection<CPU> CPUS, Statistics statistics){
		this.GPUS = GPUS;
		this.CPUS = CPUS;
		this.statistics = statistics;
	}

}
