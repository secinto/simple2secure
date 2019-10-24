package com.simple2secure.commons.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {
	private static Logger log = LoggerFactory.getLogger(TimeUtils.class);

	public static final String SIMPLE_DATE_FORMAT = "MM/dd/yyyy";
	public static final String SIMPLE_TIME_FORMAT = "MM/dd/yyyy hh:mm:ss";
	public static final String REPORT_DATE_FORMAT = "EEE MMM d HH:mm:ss zzz yyyy";
	public static final String EMAIL_DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";

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

	/**
	 *
	 * @param format
	 * @param date
	 * @return
	 */
	public static String formatDate(String format, Date date) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}

	/**
	 *
	 * @param format
	 * @param date
	 * @return
	 */
	public static Date parseDate(String format, String date) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date parsedDate = null;

		try {
			parsedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			log.error("Couldn't parse date. Reason {}", e.getMessage());
			if (format.equals(REPORT_DATE_FORMAT)) {
				dateFormat = new SimpleDateFormat(format, Locale.US);
				try {
					parsedDate = dateFormat.parse(date);
				} catch (ParseException e1) {
					log.error("Couldn't parse date using US locale. Reason {}", e.getMessage());
				}
			}
		}

		return parsedDate;
	}

	/**
	 *
	 * @param inputFormat
	 * @param outputFormat
	 * @param date
	 * @return
	 */
	public static String formatDate(String inputFormat, String outputFormat, String date) {
		Date parsedDate = parseDate(inputFormat, date);
		return formatDate(outputFormat, parsedDate);
	}

}
