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
package com.simple2secure.test.portal.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import javax.mail.MessagingException;

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

	@Autowired

	/**
	 * This is a positive test which tests a functionality of sendEmail function
	 *
	 * @throws IOException
	 */
	@Test
	public void testSendEmailPositive() throws IOException {
		User user = new User("s2s.test@secinto.com", "test", true, null, true, true);
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
		User user = new User("", "test", true, null, true, true);
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
		User user = new User(null, "test", true, null, true, true);
		boolean result = mailUtils.sendEmail(user, "Sending Test Email from the test class", "Sending Test Email from the test class");
		assertFalse(result);
	}

	@Test
	public void testSendHtmlEmailPositive() throws MessagingException {
		User user = new User("s2s.test@secinto.com", "test", true, null, false, false);
		String email = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\r\n" + "  <tr>\r\n" + "      <td>\r\n"
				+ "          <table cellspacing=\"0\" cellpadding=\"0\">\r\n" + "              <tr>\r\n"
				+ "                  <td style=\"border-radius: 2px;\" bgcolor=\"#ED2939\">\r\n"
				+ "                      <a href=\"https://www.copernica.com\" target=\"_blank\" style=\"padding: 8px 12px; border: 1px solid #ED2939;border-radius: 2px;font-family: Helvetica, Arial, sans-serif;font-size: 14px; color: #ffffff;text-decoration: none;font-weight:bold;display: inline-block;\">\r\n"
				+ "                          Click             \r\n" + "                      </a>\r\n" + "                  </td>\r\n"
				+ "              </tr>\r\n" + "          </table>\r\n" + "      </td>\r\n" + "  </tr>\r\n" + "</table>";
		boolean result = mailUtils.sendHTMLEmail(user, email, "Sending html test email from the test class");
		assertTrue(result);
	}
}
