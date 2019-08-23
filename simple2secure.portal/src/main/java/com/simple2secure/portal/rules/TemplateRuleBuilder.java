package com.simple2secure.portal.rules;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.TemplateRule;

@Service
public class TemplateRuleBuilder {
	
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	
	public Rule build(TemplateRule templateRule)
	{
		Condition condition;
		Action action;
		
		switch(templateRule.getConditionTemplate())
		{
		case "find_words":
			condition = new TemplateConditionFindWords(templateRule.getConditionParams());
			break;
			
		case "specific_domains":
			condition = new TemplateConditionSpecificDomains(templateRule.getConditionParams());
			break;
		    
		default: 
			condition = null;
		}
		
		switch(templateRule.getActionTemplate())
		{
		case "send_notification":
			action = new TemplateActionSendNotification(templateRule.getActionParams()[0]);
			break;
			
		case "send_email":
			action = new TemplateActionSendEmail(templateRule.getActionParams()[0], templateRule.getActionParams()[1]);
			break;
			
		default: 
			action = null;
		}
		
		
		autowireCapableBeanFactory.autowireBean(action);
		
		Rule rule = new RuleBuilder()
                .name(templateRule.getName())
                .description(templateRule.getDescription())
                .priority(Rule.DEFAULT_PRIORITY) // maybe add priority in TemplateRule model 
                .when(condition)
                .then(action)
                .build();
		return rule; 
	}

}
