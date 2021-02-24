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

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.utils.NotificationUtils;

/**
 *
 * @author Richard Heinz
 *
 *         Action which is used as predefined Action in the rule engine. Sends an email with the given text.
 */
@AnnotationAction(
		name_tag = "general_rules_action_name_send_notification",
		description_tag = "general_rules_action_description_send_notification",
		fact_type = RuleFactType.GENERAL)
public class TemplateActionSendNotification implements Action {

	@Autowired
	NotificationUtils notificationUtils;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */
	@AnnotationRuleParam(
			name_tag = "general_rules_action_param_notification_text",
			description_tag = "general_rules_action_param_description_notification_text",
			type = DataType._STRING)
	String notification;

	@Override
	public void execute(Facts facts) throws Exception {
		ObjectId contextId = facts.get(ObjectId.class.getName());
		if (contextId == null) {
			throw new InconsistentDataException(new Message("error_rule_engine_no_contextId_given"));
		}

		notificationUtils.addNewNotification(notification, contextId, null, false);
	}
}
