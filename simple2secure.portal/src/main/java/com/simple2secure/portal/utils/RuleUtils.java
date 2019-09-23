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

import groovy.lang.GroovyClassLoader;

@Configuration
@Component
public class RuleUtils extends com.simple2secure.commons.rules.engine.RuleUtils{

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;
	
	@Autowired
	TemplateRuleRepository templateRuleRepository;
	
	
	/*
	 * AutowireCapableBeanFactory is needed to make a new instance of the class
	 * which has been imported with groovy. Otherwise the Spring Framework in the
	 * imported class won't work
	 */
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	public List<RuleWithSourcecode> getRuleWithSourcecodeRepositoryByContextId(String contextId){
		
		return ruleWithSourcecodeRepository.findByContextId(contextId);
	}
	
	public List<TemplateRule> getTemplateRulesByContextId(String contextId)
	{
		return  templateRuleRepository.findByContextId(contextId);
	}
	
	
	/**
	 * Method to create a rule from a TemplateRule class which holds the 
	 * information (name, description, params,...) about predefined conditions/
	 * actions. The Condition and Action will be registered as Beans for Spring
	 * 
	 * @param ruleData TemplateRule object which holds the information about 
	 *                 the future rule object.
	 * @param packageNameConditons represents where the predefined 
	 * 								Conditions are saved.
	 * @param packageNameAction represents where the predefined 
	 * 								Actions are saved.
	 * 
	 * @return a Rule object which can be used for the rule engine
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * 
	 */
	public Rule buildRuleFromTemplateRuleWithBean(TemplateRule ruleData, 
			String packageNameConditonsTempates, String packageNameActionTemplates) 
					throws ClassNotFoundException, InstantiationException,
					IllegalAccessException, IllegalArgumentException, 
					IOException
	{
		
		Condition condition = buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), packageNameConditonsTempates);
		autowireCapableBeanFactory.autowireBean(condition);
		
		Action action = buildActionFromTemplateAction(ruleData.getTemplateAction(), packageNameActionTemplates);
		autowireCapableBeanFactory.autowireBean(action);
		
		return new RuleBuilder().
				name(ruleData.getName()).
				description(ruleData.getDescription()).
				when(condition).
				then(action).
				build();
	}
	
	/**
	 * Method to load sourcecode from string and creates a object which 
	 * represents a rule.
	 * 
	 * Attention: Does not support spring framework in source!
	 * 
	 * @param source which contains the sourcecode of a rule class
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object createRuleFromSourceWithBean(String source)
			throws IOException, InstantiationException, IllegalAccessException
	{
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();
			autowireCapableBeanFactory.autowireBean(rule);
			
			//log.debug("Created new rule {} with GroovyClassLoader", rule.getClass().getName());
			return rule;
		}
	}
}
