package com.simple2secure.test.portal.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.User;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.utils.MailUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestMailUtils {

	@Autowired
	MailUtils mailUtils;

	/**
	 * This is a positive test which tests a functionality of sendEmail function
	 *
	 * @throws IOException
	 */
	@Test
	public void testSendEmailPositive() throws IOException {
		User user = new User("", "emir", "sahinovic", "emir", "emir.sahinovic@secinto.com", "test", true, null, null, null, true, true);
		boolean result = mailUtils.sendEmail(user, "Sending Test Email from the test class", "Sending Test Email from the test class");
		assertTrue(result);
	}

	/**
	 * This is a negative test should return false in case the user object is null
	 *
	 * @throws IOException
	 */
	@Test
	public void testSendEmailUserNullNegative() throws IOException {
		boolean result = mailUtils.sendEmail(null, "Sending Test Email from the test class", "Sending Test Email from the test class");
		assertFalse(result);
	}

	/**
	 * This test checks if the email can be sent if the user email is empty
	 *
	 * @throws IOException
	 */
	@Test
	public void testSendEmailUserEmailEmpty() throws IOException {
		User user = new User("", "emir", "sahinovic", "emir", "", "test", true, null, null, null, true, true);
		boolean result = mailUtils.sendEmail(user, "Sending Test Email from the test class", "Sending Test Email from the test class");
		assertFalse(result);
	}

	/**
	 * This test checks if the email can be sent if the user email is null
	 *
	 * @throws IOException
	 */
	@Test
	public void testSendEmailUserEmailNull() throws IOException {
		User user = new User("", "emir", "sahinovic", "emir", null, "test", true, null, null, null, true, true);
		boolean result = mailUtils.sendEmail(user, "Sending Test Email from the test class", "Sending Test Email from the test class");
		assertFalse(result);
	}
}
