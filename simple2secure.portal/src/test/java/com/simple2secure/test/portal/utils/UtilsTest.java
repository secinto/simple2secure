package com.simple2secure.test.portal.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.utils.PortalUtils;

@RunWith(SpringRunner.class)
public class UtilsTest {

	/**
	 * This is a test for testing util function readAll which converts File to String
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testReadAll() throws IOException {
		String testingString = "\t\tThis is @@@@@@@string ||||to test\n\n\n\n\n!";
		File testFile = new File("src/test/resources/testFile.txt");
		FileUtils.touch(testFile);
		FileUtils.write(testFile, testingString);

		byte[] buffer = FileUtils.readFileToByteArray(testFile);
		Reader targetReader = new CharSequenceReader(new String(buffer));
		targetReader.close();

		String convertedSting = PortalUtils.readAll(targetReader);

		Assert.assertEquals(testingString, convertedSting);

	}

	/**
	 * This is a positive test which tests a functionality of sendEmail function
	 * @throws IOException 
	 */
	@Test
	public void testSendEmailPositive() throws IOException {
		User user = new User("","emir", "sahinovic", "emir", "emir.sahinovic@secinto.com", "test", true, null, null, null,
				true, true);
		boolean result = PortalUtils.sendEmail(user, "Sending Test Email from the test class",
				"Sending Test Email from the test class");
		Assert.assertTrue(result);
	}

	/**
	 * This is a negative test should return false in case the user object is null
	 * @throws IOException 
	 */
	@Test
	public void testSendEmailUserNullNegative() throws IOException {
		boolean result = PortalUtils.sendEmail(null, "Sending Test Email from the test class",
				"Sending Test Email from the test class");
		Assert.assertFalse(result);
	}

	/**
	 * This test checks if the email can be sent if the user email is empty
	 * @throws IOException 
	 */
	@Test
	public void testSendEmailUserEmailEmpty() throws IOException {
		User user = new User("","emir", "sahinovic", "emir", "", "test", true, null, null, null, true, true);
		boolean result = PortalUtils.sendEmail(user, "Sending Test Email from the test class",
				"Sending Test Email from the test class");
		Assert.assertFalse(result);
	}

	/**
	 * This test checks if the email can be sent if the user email is null
	 * @throws IOException 
	 */
	@Test
	public void testSendEmailUserEmailNull() throws IOException {
		User user = new User("","emir", "sahinovic", "emir", null, "test", true, null, null, null, true, true);
		boolean result = PortalUtils.sendEmail(user, "Sending Test Email from the test class",
				"Sending Test Email from the test class");
		Assert.assertFalse(result);
	}
}
