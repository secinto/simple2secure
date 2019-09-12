package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(name = "blocked email addresses",
description_de = "Regel wird ausgelöst wenn die EMail von einer der angegeben Adressen kam.",
description_en = "Rule will be triggerd if the mail was send by one of the given addresses")
public class TemplateConditionBlockedEmailAddresses extends AbtractEmailCondition {

	@AnnotationRuleParamArray(name = "email addresses",
			description_de = "EMail-Addressen welche die Regel auslösen.",
			description_en = "Mail addresses which will trigger the rule.",
			type = DataType._STRING)
	private Collection<String> addresses;
	
	@Override
	protected boolean condition(Email email) {
		if(addresses.contains(email.getFrom()))
			return true;
		return false;
	}
}
