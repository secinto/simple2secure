package com.simple2secure.portal.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.rules.EmailRulesEngine;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.PortalUtils;

import ch.maxant.rules.AbstractAction;
import ch.maxant.rules.CompileException;
import ch.maxant.rules.DuplicateNameException;
import ch.maxant.rules.Engine;
import ch.maxant.rules.NoActionFoundException;
import ch.maxant.rules.NoMatchingRuleFoundException;
import ch.maxant.rules.ParseException;
import ch.maxant.rules.Rule;

@Component
public class UpdateEmailScheduler {

	private String STORE = "imap";
	private String FOLDER = "inbox";
	private String SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
	private String SOCKET_FACTORY_PORT = "465";
	private String IMAP_AUTH = "true";

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	NotificationRepository notificationRepository;
	
	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	PortalUtils portalUtils;
	
	@Autowired 
	EmailRulesEngine emailRulesEngine;

	private static final Logger log = LoggerFactory.getLogger(UpdateEmailScheduler.class);

	@Scheduled(fixedRate = 50000)
	public void checkEmails() throws Exception {
		List<EmailConfiguration> configs = emailConfigRepository.findAll();
		if (configs != null) {
			for (EmailConfiguration cfg : configs) {
				Message[] msg = connect(cfg);
				if (msg != null) {
					
					extractEmailsFromMessages(msg, cfg.getId());
				}
			}
		}
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
											body, msg.getReceivedDate().toString());
								} else if (content instanceof MimeMultipart) {
									email = new Email(messageId.toString(), configId, msg.getMessageNumber(), msg.getSubject(), msg.getFrom()[0].toString(),
											mailUtils.getTextFromMimeMultipart((MimeMultipart) msg.getContent()), msg.getReceivedDate().toString());
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
	 */

	public Message[] connect(EmailConfiguration config) throws NumberFormatException, MessagingException {
		Properties props = new Properties();

		props.setProperty("mail.imap.ssl.enable", "true");

		// create a new session with the provided properties
		Session session = Session.getDefaultInstance(props, null);

		// connect to the store using the provided credentials
		Store store = session.getStore(STORE);

		store.connect(config.getIncomingServer(), Integer.parseInt(config.getIncomingPort()), config.getEmail(), config.getPassword());

		// store.connect(config.getIncomingServer(), Integer.parseInt(config.getIncomingPort()), config.getEmail(), config.getPassword());

		log.info("Connected to the store: " + store);

		// get inbox folder
		Folder inbox = store.getFolder(FOLDER);

		// open inbox folder to read the messages
		inbox.open(Folder.READ_ONLY);

		// retrieve the messages
		Message[] messages = inbox.getMessages();

		log.info("Messages length: " + messages.length);

		return messages;
	}

}
