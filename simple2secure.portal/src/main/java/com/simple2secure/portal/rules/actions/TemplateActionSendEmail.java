package com.simple2secure.portal.rules.actions;

import java.io.IOException;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.utils.MailUtils;

@AnnotationAction(name = "send email",
		description_de = "Sendet eine Email mit definierten Text an die angegeben Adresse.",
		description_en = "Sends an email with defined text to given address.")
public class TemplateActionSendEmail extends AbtractEmailAction{

	@Autowired
	MailUtils mailUtils;

	@AnnotationRuleParam(name = "text",
			description_de = "Text der an gegene Adresse gesendet werden soll.",
			description_en = "Text which will be send to given address",
			type = DataType._STRING)
	String text;
	
	@AnnotationRuleParam(name = "emailaddress",
			description_de = "Adresse an welche die Email versand werden soll.",
			description_en = "Address to which the email should be send",
			type = DataType._STRING)
	String emailAddress;

	@Override
	protected void action(Email email) throws IOException {
		User user = new User(emailAddress, "test", true, null, true, true); 

		mailUtils.sendEmail(user, text, text);
	
	}
}





