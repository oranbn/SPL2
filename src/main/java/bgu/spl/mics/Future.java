package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 *@INV: none
 */
public class Future<T> {
	T result;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		//TODO: implement this
		result = null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 * @PRE: none
	 * @POST: none
	 */
	public T get() {
		//TODO: implement this.
		synchronized (this) {
			while (result == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 * <p>
	 * @param result the result that needed to be resolved in the future object
	 * @return none
	 * @PRE: result == null
	 * @POST: result != null
	 */
	public synchronized void resolve (T result) {
		//TODO: implement this.
		if(result!=null & this.result==null) {
			this.result = result;
			notifyAll();
		}
	}

	/**
	 * <p>
	 * @return true if this object has been resolved, false otherwise
	 * @PRE: none
	 * @POST: @return result!=null
	 */
	public boolean isDone() {
		//TODO: implement this.
		return result!=null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 * @PRE: none
	 * @POST: @return result if future was resolved inside the timeout limit.
	 * 		  Else, if it has been resolved in the time limit, return result.
	 * 		  Else, if in the time limit it hasn't been resolved, return null.
	 */
	public T get(long timeout, TimeUnit unit) {
		if(result!=null)
			return result;
		try {
			unit.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
}
