package com.simple2secure.portal.rules.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.utils.NotificationUtils;

@AnnotationAction(name = "send notification",
description_de = "Sendet eine Notification an das Portal.",
description_en = "Sends a notification to the portal.")
public class TemplateActionSendNotification extends AbtractEmailAction{
	
	@AnnotationRuleParam(name = "text",
			description_de = "Text der als Notification angezeigt wird.",
			description_en = "Text which shown as notification",
			type = DataType._String)
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
