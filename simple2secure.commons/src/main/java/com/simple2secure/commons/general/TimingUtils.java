/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
