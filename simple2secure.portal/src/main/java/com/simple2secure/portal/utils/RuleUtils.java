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

import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.repository.RuleActionsRepository;
import com.simple2secure.portal.repository.RuleConditionsRepository;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import groovy.lang.GroovyClassLoader;

@Configuration
@Component
public class RuleUtils extends com.simple2secure.commons.rules.engine.RuleUtils {

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;

	@Autowired
	TemplateRuleRepository templateRuleRepository;
	
	@Autowired
	RuleActionsRepository templateActionRepository;
	
	@Autowired
	RuleConditionsRepository templateConditionRepository;
	
	@Autowired
	public MessageByLocaleService messageByLocaleService;
	

	/*
	 * AutowireCapableBeanFactory is needed to make a new instance of the class which has been imported with groovy. Otherwise the Spring
	 * Framework in the imported class won't work
	 */
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	
	public List<RuleWithSourcecode> getRuleWithSourcecodeRepositoryByContextId(String contextId) {
		return ruleWithSourcecodeRepository.findByContextId(contextId);
	}

	/**
	 * Method to fetch TemplateRules from the DB by contextId
	 * 
	 * @param contextId
	 * @return List of all found TemplateRules
	 */
	public List<TemplateRule> getTemplateRulesByContextId(String contextId) {
		return templateRuleRepository.findByContextId(contextId);
	}
	
	/**
	 * Method to fetch TemplateRule from the DB by contextId and ruleId
	 * 
	 * @param contextId
	 * @param ruleId
	 * @return TemplateRule object
	 */
	public TemplateRule getTemplateRulesByContextIdAndRuleId(String contextId, String ruleId) {
		return templateRuleRepository.findByContextIdAndRuleId(contextId, ruleId);
	}

	/**
	 * Method to create a rule from a TemplateRule class which holds the information (name, description, params,...) about predefined
	 * conditions/ actions. The Condition and Action will be registered as Beans for Spring
	 *
	 * @param ruleData
	 *          TemplateRule object which holds the information about the future rule object.
	 * @param packageNameConditons
	 *          represents where the predefined Conditions are saved.
	 * @param packageNameAction
	 *          represents where the predefined Actions are saved.
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
	public Rule buildRuleFromTemplateRuleWithBean(TemplateRule ruleData, String packageNameConditonsTempates,
			String packageNameActionTemplates)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException {

		Condition condition = buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), packageNameConditonsTempates, ruleData.getName());
		autowireCapableBeanFactory.autowireBean(condition);

		Action action = buildActionFromTemplateAction(ruleData.getTemplateAction(), packageNameActionTemplates);
		autowireCapableBeanFactory.autowireBean(action);

		return new RuleBuilder().name(ruleData.getName()).description(ruleData.getDescription()).when(condition).then(action).build();
	}

	/**
	 * Method to load sourcecode from string and creates a object which represents a rule.
	 *
	 * Attention: Does not support spring framework in source!
	 *
	 * @param source
	 *          which contains the sourcecode of a rule class
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object createRuleFromSourceWithBean(String source) throws IOException, InstantiationException, IllegalAccessException {
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();
			autowireCapableBeanFactory.autowireBean(rule);

			// log.debug("Created new rule {} with GroovyClassLoader", rule.getClass().getName());
			return rule;
		}
	}
	
	public void resetTextTags(TemplateRule rule)
	{
		TemplateAction action = templateActionRepository.find(rule.getTemplateAction().getId());
		
		for(int paramCount = 0; paramCount < rule.getTemplateAction().getParams().size(); paramCount++)
		{
			action.getParams().set(paramCount, RuleParam.copyAndSetValue(
					action.getParams().get(paramCount), rule.getTemplateAction().getParams().get(paramCount).getValue()));
		}
		
		for(int paramCount = 0; paramCount < rule.getTemplateAction().getParamArrays().size(); paramCount++)
		{
			action.getParamArrays().set(paramCount, RuleParamArray.copyAndSetValue(
					action.getParamArrays().get(paramCount), rule.getTemplateAction().getParamArrays().get(paramCount).getValues()));
		}
			/*
			 action.getParams().get(paramCount).setValue(
			
					(action.getParams().get(paramCount).getClass()))
					old.getTemplateAction().getParams().get(paramCount).getValue());
					*/
		
		TemplateCondition condition = templateConditionRepository.find(rule.getTemplateCondition().getId());
		
		for(int paramCount = 0; paramCount < rule.getTemplateCondition().getParams().size(); paramCount++)
		{
			condition.getParams().set(paramCount, RuleParam.copyAndSetValue(
					condition.getParams().get(paramCount), rule.getTemplateCondition().getParams().get(paramCount).getValue()));
		}
		
		for(int paramCount = 0; paramCount < rule.getTemplateCondition().getParamArrays().size(); paramCount++)
		{
			condition.getParamArrays().set(paramCount, RuleParamArray.copyAndSetValue(
					condition.getParamArrays().get(paramCount), rule.getTemplateCondition().getParamArrays().get(paramCount).getValues()));
		}
		
		rule.setTemplateAction(action);
		rule.setTemplateCondition(condition);
	}
	
	public void setLocaleTexts(TemplateAction action, ValidInputLocale locale)
	{
		try
		{
			action.setNameTag(messageByLocaleService.getMessage(action.getNameTag(), locale.getValue()));
			action.setDescriptionTag(messageByLocaleService.getMessage(action.getDescriptionTag(), locale.getValue()));
			
			action.getParams().forEach(param -> {
				param.setNameTag(messageByLocaleService.getMessage(param.getNameTag(), locale.getValue()));
				param.setDescriptionTag(messageByLocaleService.getMessage(param.getDescriptionTag(), locale.getValue()));
			});
			
			action.getParamArrays().forEach(paramArray -> {
				paramArray.setNameTag(messageByLocaleService.getMessage(paramArray.getNameTag(), locale.getValue()));
				paramArray.setDescriptionTag(messageByLocaleService.getMessage(paramArray.getDescriptionTag(), locale.getValue()));
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		
	}
	
	public void setLocaleTexts(TemplateCondition condition, ValidInputLocale locale)
	{
		try
		{
			condition.setNameTag(messageByLocaleService.getMessage(condition.getNameTag(), locale.getValue()));
			condition.setDescriptionTag(messageByLocaleService.getMessage(condition.getDescriptionTag(), locale.getValue()));
			
			condition.getParams().forEach(param -> {
				param.setNameTag(messageByLocaleService.getMessage(param.getNameTag(), locale.getValue()));
				param.setDescriptionTag(messageByLocaleService.getMessage(param.getDescriptionTag(), locale.getValue()));
			});
			
			condition.getParamArrays().forEach(paramArray -> {
				paramArray.setNameTag(messageByLocaleService.getMessage(paramArray.getNameTag(), locale.getValue()));
				paramArray.setDescriptionTag(messageByLocaleService.getMessage(paramArray.getDescriptionTag(), locale.getValue()));
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public void setLocaleTexts(TemplateRule rule, ValidInputLocale locale)
	{
		setLocaleTexts(rule.getTemplateAction(), locale);
		setLocaleTexts(rule.getTemplateCondition(), locale);
	}
	
}
