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
package com.simple2secure.commons.license;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.time.TimeUtils;

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
		return TimeUtils.formatDate(TimeUtils.SIMPLE_DATE_FORMAT, c.getTime());
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
		return TimeUtils.formatDate(TimeUtils.SIMPLE_DATE_FORMAT, calendar.getTime());
	}

	/**
	 * Checks if provided expirationDateString is already past the current date. If it is past the current date true is returned, otherwise
	 * false.
	 *
	 * @param expirationDateString
	 *          The string containing a date in the {@link #SIMPLE_DATE_FORMAT}.
	 * @return True if the provided date is past the current date.
	 * @throws ParseException
	 *           Thrown if the provided date can't be parsed due to an illegal format.
	 */
	public static boolean isLicenseExpired(String expirationDateString) {
		try {
			Date expirationDate = TimeUtils.parseDate(TimeUtils.SIMPLE_DATE_FORMAT, expirationDateString);
			return System.currentTimeMillis() > expirationDate.getTime();
		} catch (Exception e) {
			log.error("Couldn't check license expiration due to parsing error. Reason {}", e);
		}
		return true;
	}
}
