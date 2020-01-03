package com.simple2secure.portal.rules.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.portal.utils.MailUtils;

@AnnotationAction(name = "forward email", description_de = "Sendet den Inhalt der erhaltenen Mail weiter", description_en = "Forwards the received email")
public class TemplateActionForwardEmail extends AbtractEmailAction {

	@Autowired
	MailUtils mailUtils;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */
	@AnnotationRuleParam(name = "text", description_de = "Text der an die Email angeh&aumlngt werden soll.", description_en = "Text which will be attached to the mail", type = DataType._STRING)
	String text;

	@AnnotationRuleParam(name = "subject", description_de = "Betreff der Email.", description_en = "Subject of the email.", type = DataType._STRING)
	String subject;

	@AnnotationRuleParam(name = "emailaddress", description_de = "Adresse an welche die Email versand werden soll.", description_en = "Address to which the email should be send", type = DataType._STRING)
	String emailAddress;

	@Override
	protected void action(Email email) throws Exception {
		User user = new User(emailAddress, "test", true, null, true, true);

		String content = text + "\n\n" + "received email why the rule has been triggered:\n\n" + "address: \"" + email.getFrom() + "\"\n"
				+ "subject: \"" + email.getSubject() + "\"\n" + "text: \"" + email.getText() + "\"\n";

		mailUtils.sendEmail(user, content, subject);

	}

}
