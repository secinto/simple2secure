package com.simple2secure.portal.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.EmailConfigurationDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.api.model.FrontendRule;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserInvitation;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Configuration
@Component
public class MailUtils {

	@Value("${mail.username}")
	private String mailUser;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	RuleUtils ruleUtils;

	/**
	 * Sends an email with the activation token or in case of the password reset to the user
	 *
	 * @param user
	 * @throws IOException
	 */
	public boolean sendEmail(User user, String emailContent, String subject) throws IOException {
		if (user != null && !Strings.isNullOrEmpty(user.getEmail())) {
			String to = user.getEmail();
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setFrom(mailUser);
			message.setSubject(subject);
			message.setText(emailContent);
			javaMailSender.send(message);
			return true;
		}
		return false;
	}

	/**
	 * This function sends an email message as html
	 *
	 * @param user
	 * @param emailContent
	 * @param subject
	 * @return
	 * @throws MessagingException
	 */
	public boolean sendHTMLEmail(User user, String emailContent, String subject) throws MessagingException {
		if (user != null && !Strings.isNullOrEmpty(emailContent) && !Strings.isNullOrEmpty(subject)) {
			MimeMessage message = javaMailSender.createMimeMessage();
			message.setSubject(subject);
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
			mimeMessageHelper.setFrom(mailUser);
			mimeMessageHelper.setTo(user.getEmail());
			mimeMessageHelper.setText(emailContent, true);
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

	public String generateEmailContent(User user, String locale) {
		return messageByLocaleService.getMessage("registration_email_content", locale) + loadedConfigItems.getBaseURL() + "/api/user/activate/"
				+ user.getActivationToken();
	}

	/**
	 * This function generates an email body for the invitation email.
	 *
	 * @param userInvitation
	 * @param context
	 * @param addedByUser
	 * @param locale
	 * @return
	 */
	public String generateInvitationEmail(UserInvitation userInvitation, Context context, User addedByUser, String locale) {
		String content = "You have been invited by " + addedByUser.getEmail() + " to join " + context.getName()
				+ " context.\nTo accept the invitation please click on the following link: " + loadedConfigItems.getBaseURL() + "/api/user/invite/"
				+ userInvitation.getInvitationToken();
		return content;
	}

	/**
	 * This function checks if the emailConfiguration already Exists according to the email and contextId
	 *
	 * @param name
	 * @param contextId
	 * @return
	 */
	public boolean checkIfEmailConfigExists(String email, String contextId) {

		EmailConfiguration emailConfig = emailConfigRepository.findByEmailAndContextId(email.trim(), contextId);
		if (emailConfig != null) {
			return true;
		}
		return false;

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
		}
	}

	/**
	 * This function iterates over all Email Configuration according the contextId and calls the deleteEmailConfiguration function to delete
	 * each configuration
	 *
	 * @param contextId
	 */
	public void deleteEmailConfigurationByContextId(String contextId) {
		if (!Strings.isNullOrEmpty(contextId)) {
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
	public List<EmailConfigurationDTO> getEmailConfigDTO(String contextId) {
		List<EmailConfigurationDTO> configurationList = new ArrayList<>();
		List<EmailConfiguration> emailConfigList = emailConfigRepository.findByContextId(contextId);
		if (emailConfigList != null) {
			for (EmailConfiguration emailConfig : emailConfigList) {
				if (emailConfig != null) {
					List<Email> emailList = emailRepository.findByConfigId(emailConfig.getId());
					List<FrontendRule> rules = ruleUtils.getFrontendRulesByToolId(emailConfig.getId());
					EmailConfigurationDTO emailConfigDTO = new EmailConfigurationDTO(emailConfig, emailList, rules);
					configurationList.add(emailConfigDTO);
				}
			}
		}

		return configurationList;
	}

}
