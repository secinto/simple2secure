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

@AnnotationAction(name = "send email", description_de = "Sendet eine Email mit definierten Text an die angegeben Adresse.", description_en = "Sends an email with defined text to given address.")
public class TemplateActionSendEmail extends AbtractEmailAction {

	@Autowired
	MailUtils mailUtils;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */
	@AnnotationRuleParam(name = "text", description_de = "Text der an die gegebene Adresse gesendet werden soll.", description_en = "Text which will be send to given address", type = DataType._STRING)
	String text;

	@AnnotationRuleParam(name = "subject", description_de = "Betreff der Email.", description_en = "Subject of the email.", type = DataType._STRING)
	String subject;

	@AnnotationRuleParam(name = "emailaddress", description_de = "Adresse an welche die Email versendet werden soll.", description_en = "Address to which the email should be send", type = DataType._STRING)
	String emailAddress;

	@Override
	protected void action(Email email) throws IOException {
		User user = new User(emailAddress, "test", true, null, true, true);

		mailUtils.sendEmail(user, text, subject);
	}
}
