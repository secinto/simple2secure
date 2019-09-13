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


package com.simple2secure.portal.utils;

import java.io.IOException;
import java.util.List;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;

@Configuration
@Component
public class RuleUtils extends com.simple2secure.commons.rules.engine.RuleUtils{

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;
	
	@Autowired
	TemplateRuleRepository templateRuleRepository;
	
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	public List<RuleWithSourcecode> getRuleWithSourcecodeRepositoryByContextId(String contextId){
		
		return ruleWithSourcecodeRepository.findByContextId(contextId);
	}
	
	public List<TemplateRule> getTemplateRulesByContextId(String contextId)
	{
		return  templateRuleRepository.findByContextId(contextId);
	}
	
	public Rule buildRuleFromTemplateRuleWithBean(TemplateRule ruleData, 
			String pathConditonsTempates, String pathActionTemplates) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException
	{
		Condition condition = buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), pathConditonsTempates);
		autowireCapableBeanFactory.autowireBean(condition);
		
		Action action = buildActionFromTemplateAction(ruleData.getTemplateAction(), pathActionTemplates);
		autowireCapableBeanFactory.autowireBean(action);
		
		return new RuleBuilder().
				name(ruleData.getName()).
				description(ruleData.getDescription()).
				when(condition).
				then(action).
				build();
	}
}
