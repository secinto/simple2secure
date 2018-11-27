package com.simple2secure.commons.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingUtils {
	private static Logger log = LoggerFactory.getLogger(TimingUtils.class);

	private static long startTime;
	private static long stopTime;

	/**
	 *
	 * @return
	 */
	public synchronized static long startTiming() {
		startTime = System.nanoTime();
		return startTime;
	}

	/**
	 *
	 * @return
	 */
	public synchronized static double stopTimingSimple() {
		stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		double seconds = elapsedTime / 1000000000.0;
		return seconds;
	}

	/**
	 *
	 * @return
	 */
	public synchronized static double stopTiming() {
		double seconds = stopTimingSimple();
		log.info("Time between start and stop took {} seconds", seconds);
		return seconds;
	}

	/**
	 *
	 * @param startTime
	 * @return
	 */
	public synchronized static double stopTiming(long startTime) {
		TimingUtils.startTime = startTime;
		return stopTiming();
	}

	/**
	 *
	 * @param startTime
	 * @param message
	 * @return
	 */
	public synchronized static double stopTiming(long startTime, String message) {
		TimingUtils.startTime = startTime;
		return stopTiming(message);
	}

	/**
	 *
	 * @param message
	 * @return
	 */
	public synchronized static double stopTiming(String message) {
		double seconds = stopTimingSimple();
		log.info("Time for \"{}\" took {} seconds", message, seconds);
		return seconds;
	}
}
