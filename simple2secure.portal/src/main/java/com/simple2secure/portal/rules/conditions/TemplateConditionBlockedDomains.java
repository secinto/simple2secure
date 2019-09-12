package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;


@AnnotationCondition(name = "blocked domains",
description_de = "Regel wird ausgelöst wenn die EMail von einer der angegeben Domains kam.",
description_en = "Rule will be triggerd if the mail was send by one of the given domains")
public class TemplateConditionBlockedDomains extends AbtractEmailCondition {

	@AnnotationRuleParamArray(name = "domains",
			description_de = "Domains welche die Regel auslösen.",
			description_en = "Domains which will trigger the rule.",
			type = DataType._STRING)
	private Collection<String> domains;
	
	@Override
	protected boolean condition(Email email) {
		String domainFrom = email.getFrom().split("@")[1].split(">")[0]; // returns only the domain from the sender
		
		if(domains.contains(domainFrom))
			return true;
		
		return false;
	}

}
