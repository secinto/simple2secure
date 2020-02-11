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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.utils.MailUtils;

/**
 *
 * @author Richard Heinz
 *
 *         Action which is used as predefined Action in the rule engine. Sends an email with the given text.
 */
@AnnotationAction(
		name_tag = "email_rules_action_name_send_email",
		description_tag = "email_rules_action_description_send_email")
public class TemplateActionSendEmail extends AbtractEmailAction {

	@Autowired
	MailUtils mailUtils;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */
	@AnnotationRuleParam(
			name_tag = "email_rule_action_param_name_text",
			description_tag = "email_rules_action_param_description_send_text",
			type = DataType._STRING)
	String text;

	@AnnotationRuleParam(
			name_tag = "email_rules_action_param_name_subject",
			description_tag = "email_rules_action_param_description_subject",
			type = DataType._STRING)
	String subject;

	@AnnotationRuleParam(
			name_tag = "email_rules_action_param_name_emailaddress",
			description_tag = "email_rules_action_param_description_emailaddress",
			type = DataType._STRING)
	String emailAddress;

	@Override
	protected void action(Email email) throws IOException {
		User user = new User(emailAddress, "test", true, null, true, true);

		mailUtils.sendEmail(user, text, subject);
	}
}
