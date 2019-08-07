package com.simple2secure.commons.general;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

	/**
	 *
	 * @param string_url
	 * @return
	 */
	public static boolean netIsAvailable(String string_url) {
		try {
			final URL url = new URL(string_url);
			final URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
