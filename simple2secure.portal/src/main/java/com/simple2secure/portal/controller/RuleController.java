/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.repository.RuleActionsRepository;
import com.simple2secure.portal.repository.RuleConditionsRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.RuleUtils;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;
	
	@Autowired
	RuleConditionsRepository ruleConditionsRepository;
	
	@Autowired
	RuleActionsRepository ruleActionsRepository; 
	
	@Autowired
	TemplateRuleRepository templateRuleRepository;

	@Autowired
	RuleUtils ruleUtils;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/rulewithsource/", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> addOrUpdateRuleWithSourcecode(@RequestBody RuleWithSourcecode ruleWithSourcecode, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (ruleWithSourcecode != null) {
			
			if(!Strings.isNullOrEmpty(ruleWithSourcecode.getId())) {
				ruleWithSourcecodeRepository.update(ruleWithSourcecode);
			}
			else {
				ruleWithSourcecodeRepository.save(ruleWithSourcecode);
			}

			return new ResponseEntity<RuleWithSourcecode>(ruleWithSourcecode, HttpStatus.OK);
		
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/templaterule/", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> addOrUpdateTemplateRule(@RequestBody TemplateRule templateRule, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (templateRule != null) {
			
			if(!Strings.isNullOrEmpty(templateRule.getId())) {
				templateRuleRepository.update(templateRule);
			}
			else {
				templateRuleRepository.save(templateRule);
			}

			return new ResponseEntity<TemplateRule>(templateRule, HttpStatus.OK);
		
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/templaterule/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TemplateRule>> getTemplateRulesByContxtId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<TemplateRule> templateRules = ruleUtils.getTemplateRulesByContextId(contextId);

			if (templateRules != null) {
				return new ResponseEntity<List<TemplateRule>>(templateRules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/rulewithsource/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<RuleWithSourcecode>> getRulesWithSourcecodeByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<RuleWithSourcecode> ruleWithSourcecodes = ruleUtils.getRuleWithSourcecodeRepositoryByContextId(contextId);

			if (ruleWithSourcecodes != null) {
				return new ResponseEntity<List<RuleWithSourcecode>>(ruleWithSourcecodes, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/template_conditions/", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateCondition>> getTemplateConditions(
	String rule_templates,@RequestHeader("Accept-Language") String locale) { 
		Collection<TemplateCondition> conditions;
		try {
			conditions = ruleConditionsRepository.findAll();
			if(conditions == null)
			{
				conditions = ruleUtils.loadTemplateConditions("com.simple2secure.portal.rules.conditions");
				conditions.forEach(ruleConditionsRepository::save);
			}
			    
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Failed to load predefined contitions"),
					HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<Collection<TemplateCondition>>(conditions, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/template_actions/", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateAction>> getTemplateActions(
	String rule_templates,@RequestHeader("Accept-Language") String locale) { 
		Collection<TemplateAction> actions;
		try {
			actions = ruleActionsRepository.findAll();
			if(actions == null)
			{
				actions = ruleUtils.loadTemplateActions("com.simple2secure.portal.rules.actions");
				actions.forEach(ruleActionsRepository::save);
			}
			    
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Failed to load predefined contitions"),
					HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<Collection<TemplateAction>>(actions, HttpStatus.OK);
	}
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/rulewithsource/{ruleId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> deleteRuleWithSourcecode(@PathVariable("ruleId") String ruleId,
			@RequestHeader("Accept-Language") String locale){
		
		if(!Strings.isNullOrEmpty(ruleId) && !Strings.isNullOrEmpty(locale)) {
			RuleWithSourcecode ruleWithSourcecode = ruleWithSourcecodeRepository.find(ruleId);			
			if(ruleWithSourcecode != null) {
				ruleWithSourcecodeRepository.delete(ruleWithSourcecode);
				return new ResponseEntity<RuleWithSourcecode>(ruleWithSourcecode, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale)),
				HttpStatus.NOT_FOUND);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/templaterule/{ruleId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> deleteTemplateRule(@PathVariable("ruleId") String ruleId,
			@RequestHeader("Accept-Language") String locale){
		
		if(!Strings.isNullOrEmpty(ruleId) && !Strings.isNullOrEmpty(locale)) {
			TemplateRule templateRule = templateRuleRepository.find(ruleId);			
			if(templateRule != null) {
				templateRuleRepository.delete(templateRule);
				return new ResponseEntity<TemplateRule>(templateRule, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale)),
				HttpStatus.NOT_FOUND);
		
	}

}
