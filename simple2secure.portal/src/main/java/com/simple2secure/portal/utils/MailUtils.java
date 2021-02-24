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
package com.simple2secure.portal.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.EmailConfigurationDTO;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Component
@Slf4j
public class MailUtils extends BaseServiceProvider {

	@Value("${mail.username}")
	private String mailUser;

	@Value("${mail.password}")
	private String mailPassword;

	@Value("${mail.imap.host}")
	private String mailIMAPHost;

	@Value("${mail.smtp.host}")
	private String mailSMTPHost;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	RuleUtils ruleUtils;

	private boolean initialized = false;

	@PostConstruct
	public void initialize() {
		if (!Strings.isNullOrEmpty(mailUser) && !Strings.isNullOrEmpty(mailPassword) && !Strings.isNullOrEmpty(mailIMAPHost)
				&& !Strings.isNullOrEmpty(mailSMTPHost)) {
			initialized = true;
		} else {
			log.error("Mail configuration not provided. Can't send any emails from portal");
		}
	}

	/**
	 * Sends an email with the activation token or in case of the password reset to the user
	 *
	 * @param user
	 * @throws IOException
	 */
	public boolean sendEmail(String toEmail, String emailContent, String subject) throws IOException {
		if (!initialized) {
			log.warn("Not sending any email since MailUtils are not initialized correctly");
			return true;
		}

		if (!Strings.isNullOrEmpty(toEmail)) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setFrom(mailUser);
			message.setSubject(subject);
			message.setText(emailContent);
			javaMailSender.send(message);
			return true;
		}
		return false;
	}

	/**
	 * This function converts the email body from the MimeMultiPart to the string.
	 *
	 * @param mimeMultipart
	 * @return
	 * @throws Exception
	 */
	public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
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
	 * This function checks if the emailConfiguration already Exists according to the email and contextId
	 *
	 * @param name
	 * @param contextId
	 * @return
	 */
	public ObjectId checkIfEmailConfigExists(EmailConfiguration providedConfig) {

		EmailConfiguration emailConfig = emailConfigRepository.findByEmailAndContextId(providedConfig.getEmail().trim(),
				providedConfig.getContextId());
		if (emailConfig != null) {
			return emailConfig.getId();
		}
		return null;

	}

	/**
	 * This function deletes an email configuration object and its all dependencies
	 *
	 * @param emailConfiguration
	 */
	public void deleteEmailConfiguration(EmailConfiguration emailConfiguration) {
		if (emailConfiguration != null) {
			emailConfigRepository.delete(emailConfiguration);
			emailRepository.deleteByConfigId(emailConfiguration.getId());
			ruleEmailConfigMappingRepository.deleteByEmailConfig(emailConfiguration.getContextId(), emailConfiguration.getId());
		}
	}

	/**
	 * This function iterates over all Email Configuration according the contextId and calls the deleteEmailConfiguration function to delete
	 * each configuration
	 *
	 * @param contextId
	 */
	public void deleteEmailConfigurationByContextId(ObjectId contextId) {
		if (contextId != null) {
			List<EmailConfiguration> emailConfigList = emailConfigRepository.findByContextId(contextId);
			if (emailConfigList != null) {
				for (EmailConfiguration emailConfig : emailConfigList) {
					if (emailConfig != null) {
						deleteEmailConfiguration(emailConfig);
					}
				}
			}
		}
	}

	/**
	 * This function retrieves all configuration according to the contextId. It iterates over the retrieved configs and retrieves emails for
	 * that config. After that new DTO object will be constructed with config and emails.
	 *
	 * @param contextId
	 * @return
	 */
	public List<EmailConfigurationDTO> getEmailConfigDTO(ObjectId contextId, int size, int page) {
		List<EmailConfigurationDTO> configurationList = new ArrayList<>();
		List<EmailConfiguration> emailConfigList = emailConfigRepository.findByContextId(contextId);
		if (emailConfigList != null) {
			for (EmailConfiguration emailConfig : emailConfigList) {
				if (emailConfig != null) {
					List<Email> emailList = emailRepository.findByConfigId(emailConfig.getId());
					EmailConfigurationDTO emailConfigDTO = new EmailConfigurationDTO(emailConfig, emailList);
					configurationList.add(emailConfigDTO);
				}
			}
		}

		return configurationList;
	}

}
