package com.simple2secure.portal.rules;

import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.Email;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.utils.NotificationUtils;


public class TemplateActionSendNotification extends TemplateEmailAction{
	
	String notification;
	
	@Autowired	
	NotificationUtils notificationUtils;
	
	@Autowired	
	EmailConfigurationRepository emailConfigRepository;
	
	public TemplateActionSendNotification(String notification) {
		this.notification = notification;
	}

	@Override
	protected void action(Email email) throws Exception {
		String contextID = 
				emailConfigRepository.find(email.getConfigId()).getContextId();	
		notificationUtils.addNewNotificationPortal(notification, 
				contextID);	
	}
}
