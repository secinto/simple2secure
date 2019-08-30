package com.simple2secure.portal.rules.conditions;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;


@AnnotationCondition(name = "find domains",
description_de = "Regel wird ausgel�st wenn die Email von einer definierten Domain stammt",
description_en = "Rule will be triggerd if the email came form a defined domain")
public class TemplateConditionSpecificDomains extends AbtractEmailCondition
{
	@AnnotationRuleParamArray(name = "domains",
			description_de = "Definierte Domains um die Regel auszul�sen",
			description_en = "Defined domains to trigger the rule",
			type = DataType._String)
	String[] specificDomains;
	
	
	public TemplateConditionSpecificDomains(String[] specificDomains) {
		this.specificDomains = specificDomains;
	}

	@Override
	protected boolean condition(Email email) {
        String fromDomain = email.getFrom().split("@")[1].split(">")[0]; // returns only the domain from the sender
	
		for(String specificDomain : specificDomains)
		{
			if (fromDomain.contentEquals(specificDomain)) 
				return true;			
		}
		return false;
	}
}
