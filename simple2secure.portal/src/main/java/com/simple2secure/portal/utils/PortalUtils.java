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
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.User;

public class PortalUtils {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Helper function to read string from the file
	 * 
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Sends an email with the activation token or in case of the password reset to the user
	 * 
	 * @param user
	 * @throws IOException 
	 */
	public static boolean sendEmail(User user, String emailContent, String subject) throws IOException {
		if (user != null && !Strings.isNullOrEmpty(user.getEmail())) {
			String to = user.getEmail();

			Properties properties = new Properties();
			
			
			properties.load(PortalUtils.class.getClassLoader().getResourceAsStream("mail.properties"));
			
			final String username = properties.getProperty("username");
			final String password = properties.getProperty("password");

			Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject(subject);
				message.setText(emailContent);

				Transport.send(message);
				return true;
			} catch (MessagingException mex) {
				mex.printStackTrace();
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * This function generates an activation token for each user
	 * 
	 * @return
	 */
	public static synchronized String generateToken() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	public static String alphaNumericString(int len) {
	    String ALPHA_UPPER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String ALPHA_LOWER_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
	    String NUMERIC_CHARACTERS = "0123456789";
	    String SPECIAL_CHARACTERS = ";!.-:";
	    
	    Random rnd = new Random();

	    StringBuilder sb = new StringBuilder(len);
	    for (int i = 0; i < len; i++) {
	    	if(i == 0) {
	    		sb.append(ALPHA_UPPER_CHARACTERS.charAt(rnd.nextInt(ALPHA_UPPER_CHARACTERS.length())));
	    	}
	    	if(i == 1) {
	    		sb.append(NUMERIC_CHARACTERS.charAt(rnd.nextInt(NUMERIC_CHARACTERS.length())));
	    	}
	    	if(i == 2) {
	    		sb.append(SPECIAL_CHARACTERS.charAt(rnd.nextInt(SPECIAL_CHARACTERS.length())));
	    	}
	    	else {
	    		sb.append(ALPHA_LOWER_CHARACTERS.charAt(rnd.nextInt(ALPHA_LOWER_CHARACTERS.length())));
	    	}	        
	    }
	    return sb.toString();
	}
	
	/**
	 * This function converts the email body from the MimeMultiPart to the string.
	 * @param mimeMultipart
	 * @return
	 * @throws Exception
	 */
	public static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
	    String result = "";
	    int partCount = mimeMultipart.getCount();
	    for (int i = 0; i < partCount; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	            result = html;
	        } else if (bodyPart.getContent() instanceof MimeMultipart) {
	            result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
	        }
	    }
	    return result;
	}
	
	/** 
	 * This function returns the expiration date for the default group which is 
	 * created when new user is registered
	 * @return
	 */
	public static String getDefaultLicenseExpirationDate() {
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		//TODO: implement in web so that superadmin can configure this value!
		c.add(Calendar.DATE, 10);
		Date currentDatePlusFive = c.getTime();
		return sdf.format(currentDatePlusFive);	
	}
	
	/**
	 * This function checks if processor with the provided name or class already exist in the database. 
	 * New processor will be only added if it does not exist.
	 * @param processors
	 * @param processor
	 * @return
	 */
	public static boolean checkIfListAlreadyContainsProcessor(List<Processor> processors, Processor processor) {
		for(Processor processor_item : processors) {
			if(processor_item.getName().trim().equals(processor.getName().trim())) {
				return true;
			}
			if(processor_item.getProcessor_class().trim().equals(processor.getProcessor_class().trim())) {
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Check before each request if access token has expired
	 * @param expirationDate
	 * @return
	 */
	public static boolean isAccessTokenExpired(Date expirationDate) {
		Date currentDate = new Date(System.currentTimeMillis());

		if (expirationDate.before(currentDate)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static Date convertStringtoDate(String date) {
		try {
			return DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isLicenseExpired(String expDate) {
		Date expirationDate = convertStringtoDate(expDate);
		return System.currentTimeMillis() > expirationDate.getTime();
	}
	
	public static long convertTimeUnitsToMilis(long time, TimeUnit timeUnit) {
		if(timeUnit != null) {
			if(timeUnit.equals(TimeUnit.SECONDS)) {
				return TimeUnit.SECONDS.toMillis(time);
			}
			else if(timeUnit.equals(TimeUnit.MINUTES)) {
				return TimeUnit.MINUTES.toMillis(time);
			}
			else if(timeUnit.equals(TimeUnit.HOURS)) {
				return TimeUnit.HOURS.toMillis(time);
			}
			else if(timeUnit.equals(TimeUnit.DAYS)) {
				return TimeUnit.DAYS.toMillis(time);
			}
			else {
				return 0;
			}
		}
		else {
			return 0;
		}
	}
}
