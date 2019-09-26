package com.simple2secure.commons.time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

	/**
	 * Check before each request if access token has expired
	 *
	 * @param expirationDate
	 * @return
	 */
	public static boolean isExpired(Date expirationDate) {
		Date currentDate = new Date(System.currentTimeMillis());

		if (expirationDate.before(currentDate)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Converts the given TimeUnits time to milliseconds.
	 *
	 * @param time
	 *          The value of the time unit.
	 * @param timeUnit
	 *          The unit in which the time is measured.
	 * @return The specified amount of time in milliseconds.
	 */
	public static long convertTimeUnitsToMilis(long time, TimeUnit timeUnit) {
		if (timeUnit != null) {
			return timeUnit.toMillis(time);
		} else {
			return 0;
		}
	}

}
