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
			type = DataType._STRING)
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
