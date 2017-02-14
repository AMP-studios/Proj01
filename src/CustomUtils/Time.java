package CustomUtils;

import org.apache.commons.lang3.time.StopWatch;

/**
 * Created by Pavel on 12/2/2016.
 * Contains a single stopwatch which can be used to keep track of timers, along with other useful features.
 */
public class Time {
	private StopWatch sw=new StopWatch();

	/**
	 * Starts or resumes the stopwatch.
	 */
	public void start() {
		try {
			sw.start();
		} catch (IllegalStateException e) {
			if(sw.isStopped())
			{
				sw.resume();
			}
		}
	}

	/**
	 * Pauses the stopwatch
	 */
	@SuppressWarnings("unused")
	public void stop(){
		try {
			sw.suspend();
		} catch (IllegalStateException ignored){}
	}

	/**
	 * Get the current time on the stopwatch
	 * @return long millis: the amount of milliseconds recorded on the stopwatch.
	 */
	public long getTime() {
		sw.split();
		long millis=sw.getSplitTime();
		sw.unsplit();
		return millis;
	}
	/**
	 * Resets the stopwatch to 0 and stops it.
	 */
	public void clear() {
		sw.reset();
	}
	public boolean isStarted() {return sw.isStarted();}
}
