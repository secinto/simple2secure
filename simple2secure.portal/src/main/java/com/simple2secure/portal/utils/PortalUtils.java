/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.utils;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Processor;

@Component
public class PortalUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	@Autowired
	JavaMailSender javaMailSender;

	/**
	 * Helper function to read string from the file
	 *
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	public String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * This function generates an activation token for each user
	 *
	 * @return
	 */
	public synchronized String generateToken() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public String alphaNumericString(int len) {
		String ALPHA_UPPER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String ALPHA_LOWER_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
		String NUMERIC_CHARACTERS = "0123456789";
		String SPECIAL_CHARACTERS = ";!.-:";

		Random rnd = new Random();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			if (i == 0) {
				sb.append(ALPHA_UPPER_CHARACTERS.charAt(rnd.nextInt(ALPHA_UPPER_CHARACTERS.length())));
			}
			if (i == 1) {
				sb.append(NUMERIC_CHARACTERS.charAt(rnd.nextInt(NUMERIC_CHARACTERS.length())));
			}
			if (i == 2) {
				sb.append(SPECIAL_CHARACTERS.charAt(rnd.nextInt(SPECIAL_CHARACTERS.length())));
			} else {
				sb.append(ALPHA_LOWER_CHARACTERS.charAt(rnd.nextInt(ALPHA_LOWER_CHARACTERS.length())));
			}
		}
		return sb.toString();
	}

	/**
	 * This function returns the expiration date for the default group which is created when new user is registered
	 *
	 * @return
	 */
	public String getDefaultLicenseExpirationDate() {
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		// TODO: implement in web so that superadmin can configure this value!
		c.add(Calendar.DATE, 10);
		Date currentDatePlusFive = c.getTime();
		return sdf.format(currentDatePlusFive);
	}

	/**
	 * This function checks if processor with the provided name or class already exist in the database. New processor will be only added if it
	 * does not exist.
	 *
	 * @param processors
	 * @param processor
	 * @return
	 */
	public boolean checkIfListAlreadyContainsProcessor(List<Processor> processors, Processor processor) {
		for (Processor processor_item : processors) {
			if (processor_item.getName().trim().equals(processor.getName().trim())) {
				return true;
			}
			if (processor_item.getProcessor_class().trim().equals(processor.getProcessor_class().trim())) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Check before each request if access token has expired
	 *
	 * @param expirationDate
	 * @return
	 */
	public boolean isAccessTokenExpired(Date expirationDate) {
		Date currentDate = new Date(System.currentTimeMillis());

		if (expirationDate.before(currentDate)) {
			return true;
		} else {
			return false;
		}

	}

	public Date convertStringtoDate(String date) {
		try {
			return DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isLicenseExpired(String expDate) {
		Date expirationDate = convertStringtoDate(expDate);
		return System.currentTimeMillis() > expirationDate.getTime();
	}

	public long convertTimeUnitsToMilis(long time, TimeUnit timeUnit) {
		if (timeUnit != null) {
			if (timeUnit.equals(TimeUnit.SECONDS)) {
				return TimeUnit.SECONDS.toMillis(time);
			} else if (timeUnit.equals(TimeUnit.MINUTES)) {
				return TimeUnit.MINUTES.toMillis(time);
			} else if (timeUnit.equals(TimeUnit.HOURS)) {
				return TimeUnit.HOURS.toMillis(time);
			} else if (timeUnit.equals(TimeUnit.DAYS)) {
				return TimeUnit.DAYS.toMillis(time);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
