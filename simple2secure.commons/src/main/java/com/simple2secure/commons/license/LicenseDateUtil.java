package com.simple2secure.commons.license;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseDateUtil {

	private static Logger log = LoggerFactory.getLogger(LicenseDateUtil.class);

	/**
	 * Returns the default expiration date for a license counted from the current date. The default expiration date is one month.
	 *
	 * @return The expiration date as string representation
	 */
	public static String getDefaultLicenseExpirationDate() {
		return getLicenseExpirationDate(Calendar.MONTH, 1);
	}

	/**
	 * This function returns the expiration date from the current date adding the given amount of time. The field can be either one of
	 * <ul>
	 * <li>Calendar.DAY_OF_MONTH</li>
	 * <li>Calendar.MONTH</li>
	 * <li>Calendar.YEAR</li>
	 * </ul>
	 * The value specifies the amount added to the given field from the current date.
	 *
	 * @return The expiration date as string representation
	 */
	public static String getLicenseExpirationDate(int field, int value) {
		return getLicenseExpirationDate(new Date(), field, value);
	}

	/**
	 * This function returns the expiration date from the given date adding the given amount of time. @see
	 * {@link #getLicenseExpirationDate(int, int)} for more information about what fields can be used.
	 *
	 * @param date
	 *          The to which the amount specified through field and value should be added
	 * @param field
	 *          The type of {@link Calendar} field which should be used as basis for adding
	 * @param value
	 *          The amount of unit which should be added to the given date
	 * @return The license expiration date as string representation
	 */
	public static String getLicenseExpirationDate(Date date, int field, int value) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, value);
		return convertDateToLicenseFormatString(c.getTime());
	}

	/**
	 * Get the license expiration date from the provided TimeUnit time.
	 *
	 * @param time
	 *          The validity period specified in {@link TimeUnit} units.
	 * @param unit
	 *          The {@link TimeUnit} which should be used for mapping the time value.
	 * @return The license expiration date as string representation converted from {@link TimeUnit},
	 */
	public static String getLicenseExpirationDate(long time, TimeUnit unit) {
		return getLicenseExpirationDate(time, unit, Calendar.MONTH, 0);
	}

	/**
	 * Get the license expiration date from the provided TimeUnit time and adding the amount specified through {@link Calendar} field and
	 * value. @see {@link #getLicenseExpirationDate(int, int)} for more information about what fields can be used.
	 *
	 * @param time
	 *          The validity period specified in {@link TimeUnit} units.
	 * @param unit
	 *          The {@link TimeUnit} which should be used for mapping the time value.
	 * @return The license expiration date as string representation converted from {@link TimeUnit},
	 */
	public static String getLicenseExpirationDate(long time, TimeUnit unit, int field, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + unit.toMillis(time));
		return convertDateToLicenseFormatString(calendar.getTime());
	}

	/**
	 * Creates the license expiration date in String representation from the given expiration date as {@link Date}.
	 *
	 * @param expirationDate
	 *          The desired license expiration date.
	 * @return The date as string representation in the {@link #LICENSE_DATE_FORMAT} date format.
	 */
	public static String convertDateToLicenseFormatString(Date expirationDate) {
		return new SimpleDateFormat(License.LICENSE_DATE_FORMAT).format(expirationDate);
	}

	/**
	 * Converts the provided string to a {@link Date} object assuming the {@link #LICENSE_DATE_FORMAT} date format.
	 *
	 * @param date
	 *          The date as string which should be converted
	 * @return The {@link Date} object created from the provided string.
	 * @throws ParseException
	 *           Thrown if the provided date can't be parsed due to an illegal format.
	 */
	public static Date convertLicenseFormatStringToDate(String date) throws ParseException {
		return new SimpleDateFormat(License.LICENSE_DATE_FORMAT).parse(date);
	}

	/**
	 * Checks if provided expirationDateString is already past the current date. If it is past the current date true is returned, otherwise
	 * false.
	 *
	 * @param expirationDateString
	 *          The string containing a date in the {@link #LICENSE_DATE_FORMAT}.
	 * @return True if the provided date is past the current date.
	 * @throws ParseException
	 *           Thrown if the provided date can't be parsed due to an illegal format.
	 */
	public static boolean isLicenseExpired(String expirationDateString) {
		try {
			Date expirationDate = convertLicenseFormatStringToDate(expirationDateString);
			return System.currentTimeMillis() > expirationDate.getTime();
		} catch (Exception e) {
			log.error("Couldn't check license expiration due to parsing error. Reason {}", e);
		}
		return true;
	}
}
