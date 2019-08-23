package com.simple2secure.portal.rules;

import com.simple2secure.api.model.Email;

public class TemplateConditionSpecificDomains extends TemplateEmailCondition
{
	
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
