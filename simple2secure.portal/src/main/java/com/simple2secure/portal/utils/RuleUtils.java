package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.GroovyRule;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.repository.GroovyRuleRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;

@Configuration
@Component
public class RuleUtils extends com.simple2secure.commons.rules.engine.RuleUtils{

	@Autowired
	GroovyRuleRepository groovyRuleRepository;
	
	@Autowired
	TemplateRuleRepository templateRuleRepository;
	
	public List<GroovyRule> getGroovyRulesByContextId(String contextId){
		
		return groovyRuleRepository.findByContextId(contextId);
	}
	
	public List<TemplateRule> getTemplateRulesByContextId(String contextId)
	{
		return  templateRuleRepository.findByContextId(contextId);
	}
}
