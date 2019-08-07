/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
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
