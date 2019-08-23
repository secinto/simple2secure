/**
*********************************************************************
*   simple2secure is a cyber risk and information security platform.
*   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
*********************************************************************
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*  
 *********************************************************************
*/

package com.simple2secure.portal.rules;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.Rule;
import com.simple2secure.api.model.TemplateRule;
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
				log.debug("Unable to load Rule " + rule.getName() + " " + e.getMessage());
				// e.printStackTrace();
			}
		});
		
		checkFacts();
		
		removeFact(email.getClass().getName());
		rules_.clear();
	}
}
