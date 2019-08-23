package com.simple2secure.portal.rules;

import java.io.IOException;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.NotificationUtils;

public class TemplateActionSendEmail extends TemplateEmailAction{

	@Autowired
	MailUtils mailUtils;

	String text;
	String emailAddress;
	
	
	public TemplateActionSendEmail(String text, String emailAddress) {
		this.text = text;
		this.emailAddress = emailAddress;
	}

	@Override
	protected void action(Email email) throws IOException {
		User user = new User(emailAddress, "test", true, null, true, true); 
		boolean result = mailUtils.sendEmail(user, text, text);
	}
}





