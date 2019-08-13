package com.simple2secure.portal.rules;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.Rule;
import com.simple2secure.portal.utils.RuleUtils;

@Service
public class EmailRulesEngine extends PortalRulesEngine {
	
	@Autowired
	private RuleUtils ruleUtils;
	
	private static Logger log = LoggerFactory.getLogger(EmailRulesEngine.class);
	
	public void checkMail(Email email, String contextId)
	{   
		addFact(email);
		List<Rule> rules = ruleUtils.getRulesByContextId(contextId);
		
		rules.forEach(rule -> {
			try {
				addRuleFromSourceWithBean(rule.getGroovyCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		checkFacts();
		
		removeFact(email.getClass().getName());
		rules_.clear();
	}

}
