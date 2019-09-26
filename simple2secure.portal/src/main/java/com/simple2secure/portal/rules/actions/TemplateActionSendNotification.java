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

/**
 *
 * @author Richard Heinz
 *
 *         Action which is used as predefined Action in the rule engine. Sends an email with the given text.
 */
@AnnotationAction(
		name = "send notification",
		description_de = "Sendet eine Benachrichtigung an das Portal.",
		description_en = "Sends a notification to the portal.")
public class TemplateActionSendNotification extends AbtractEmailAction {

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */
	@AnnotationRuleParam(
			name = "text",
			description_de = "Text der in der Benachrichtung enthalten sein soll.",
			description_en = "Text which is shown as notification",
			type = DataType._STRING)
	String notification;

	@Override
	protected void action(Email email) throws Exception {
		String contextID = emailConfigRepository.find(email.getConfigId()).getContextId();

		notificationUtils.addNewNotificationPortal(notification, contextID);
	}
}
