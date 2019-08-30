package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.GroovyRule;
import com.simple2secure.portal.repository.RuleRepository;

@Configuration
@Component
public class RuleUtils {

	@Autowired
	RuleRepository ruleRepository;
	
	public List<GroovyRule> getRulesByContextId(String contextId){
		return ruleRepository.findByContextId(contextId);
	}
}
