package com.simple2secure.portal.rules.actions;

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
 *         Action which is used as predefined Action in the rule engine. Sends the received email.
 */
@AnnotationAction(
		name_tag = "email_rules_action_name_forward_email",
		description_tag = "email_rules_action_description_forward_email")

public class TemplateActionForwardEmail extends AbtractEmailAction {

	@Autowired
	MailUtils mailUtils;

	/*
	 * All field values which are annotated as AnnotationRuleParam or AnnotationRuleParamArray are filled/saved during runtime directly.
	 */

	@AnnotationRuleParam(
			name_tag = "email_rules_action_param_name_attach_text",
			description_tag = "email_rules_action_param_description_attach_text",
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
	protected void action(Email email) throws Exception {
		User user = new User(emailAddress, "test", true, null, true, true);

		String content = text + "\n\n" + "received email why the rule has been triggered:\n\n" + "address: \"" + email.getFrom() + "\"\n"
				+ "subject: \"" + email.getSubject() + "\"\n" + "text: \"" + email.getText() + "\"\n";

		mailUtils.sendEmail(user, content, subject);

	}

}
