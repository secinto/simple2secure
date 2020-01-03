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
package com.simple2secure.portal.scheduler;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.api.model.Status;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.rules.EmailRulesEngine;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.PortalUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UpdateEmailScheduler {

	private String STORE = "imap";
	private String FOLDER = "inbox";

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	EmailRulesEngine emailRulesEngine;

	private Properties emailProperties;

	private Hashtable<String, Store> storeMapping = new Hashtable<>();

	public UpdateEmailScheduler() {
		emailProperties = new Properties();
		emailProperties.setProperty("mail.imap.ssl.enable", "true");
	}

	@Scheduled(fixedRate = 50000)
	public void checkEmails() throws Exception {
		List<EmailConfiguration> configs = emailConfigRepository.findAll();
		log.info("Checking configured email inboxes");
		if (configs != null) {
			for (EmailConfiguration cfg : configs) {
				cfg.setCurrentStatus(Status.CHECKING);
				emailConfigRepository.update(cfg);
				Message[] msg = getMessagesForStore(getConnection(cfg), cfg);
				cfg.setCurrentStatus(Status.CONNECTED);
				emailConfigRepository.update(cfg);
				if (msg != null && msg.length > 0) {
					cfg.setCurrentStatus(Status.SYNCHING);
					emailConfigRepository.update(cfg);
					extractEmailsFromMessages(msg, cfg.getId());
				}
			}
		}
		log.info("Checking configured email inboxes finished");
	}

	/**
	 * This function extracts the emails from the message array, and converts the email content from MimeMultipart type to the String
	 *
	 * @param messages
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public void extractEmailsFromMessages(Message[] messages, String configId) {
		if (!Strings.isNullOrEmpty(configId)) {
			EmailConfiguration emailConfig = emailConfigRepository.find(configId);
			if (emailConfig != null) {
				for (Message msg : messages) {
					UIDFolder uf = (UIDFolder) msg.getFolder();
					Long messageId;
					try {
						messageId = uf.getUID(msg);
						if (emailRepository.findByConfigAndMessageId(configId, messageId.toString()) == null) {
							// TO-DO - check if there is a rule for this inbox and check it accordingly
							Email email = new Email();
							Object content;
							try {
								content = msg.getContent();
								if (content instanceof String) {
									String body = (String) content;
									email = new Email(messageId.toString(), configId, msg.getMessageNumber(), msg.getSubject(), msg.getFrom()[0].toString(),
											body, msg.getReceivedDate());
								} else if (content instanceof MimeMultipart) {
									email = new Email(messageId.toString(), configId, msg.getMessageNumber(), msg.getSubject(), msg.getFrom()[0].toString(),
											mailUtils.getTextFromMimeMultipart((MimeMultipart) msg.getContent()), msg.getReceivedDate());
								}

								emailRulesEngine.checkMail(email, emailConfig.getContextId());

								emailRepository.save(email);
							} catch (Exception e) {
								log.error("Problem occured {}", e.getMessage());
							}
						}

					} catch (MessagingException e1) {
						log.error("Problem occured messageId not found");
					}
				}
			}
		}
	}

	/**
	 * This function connects to the server and returns the messages in case that the connection was successful
	 *
	 * @param config
	 * @return
	 * @throws NumberFormatException
	 * @throws MessagingException
	 * @throws ItemNotFoundRepositoryException
	 */

	public Store getConnection(EmailConfiguration config) throws NumberFormatException, MessagingException, ItemNotFoundRepositoryException {

		if (storeMapping.containsKey(config.getId())) {
			return storeMapping.get(config.getId());
		} else {

			// create a new session with the provided properties
			Session session = Session.getDefaultInstance(emailProperties, null);

			// connect to the store using the provided credentials
			Store store = session.getStore(STORE);

			store.connect(config.getIncomingServer(), Integer.parseInt(config.getIncomingPort()), config.getEmail(), config.getPassword());
			log.debug("Connection to store {} established " + store);

			storeMapping.put(config.getId(), store);
			return store;
		}
	}

	public Message[] getMessagesForStore(Store store, EmailConfiguration config) throws MessagingException, ItemNotFoundRepositoryException {
		// get inbox folder
		Folder inbox = store.getFolder(FOLDER);

		// open inbox folder to read the messages
		inbox.open(Folder.READ_ONLY);

		Message[] messages = new Message[0];
		int newEnd = inbox.getMessageCount();
		if (config.getLastEnd() == 0) {
			messages = inbox.getMessages();
			config.setLastEnd(newEnd);
			emailConfigRepository.update(config);
		} else if (config.getLastEnd() != newEnd) {
			messages = inbox.getMessages(config.getLastEnd(), newEnd);
			config.setLastEnd(newEnd);
			emailConfigRepository.update(config);
		}
		log.debug("{} messages obtained from IMAP server {} for email address{} ", messages.length, config.getIncomingServer(),
				config.getEmail());

		return messages;

	}

}
