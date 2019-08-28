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
import com.google.gson.Gson;
import com.simple2secure.api.model.Rule;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.rules.TemplateRuleBuilder;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.RuleUtils;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	RuleUtils ruleUtils;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Rule> addOrUpdateRule(@RequestBody Rule rule, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (rule != null) {
			
			if(!Strings.isNullOrEmpty(rule.getId())) {
				ruleRepository.update(rule);
			}
			else {
				ruleRepository.save(rule);
			}

			return new ResponseEntity<Rule>(rule, HttpStatus.OK);
		
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Rule>> getRulesByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<Rule> rules = ruleUtils.getRulesByContextId(contextId);

			if (rules != null) {
				return new ResponseEntity<List<Rule>>(rules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/rule_templates", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateCondition>> getRulesTemplates(
	String rule_templates,@RequestHeader("Accept-Language") String locale) { 
		Collection<TemplateCondition> conditions;
		try {
			conditions = TemplateRuleBuilder.loadTemplatesConditions("com.simple2secure.portal.rules");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Failed to load predefined contitions"),
					HttpStatus.FAILED_DEPENDENCY);
		}

		//String[] strings = {new Gson().toJson(conditions)};
		return new ResponseEntity<Collection<TemplateCondition>>(conditions, HttpStatus.OK);
		//return new ResponseEntity<String[]>(strings, HttpStatus.OK);
	}
	
	//"/delete/{ruleId}"
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{ruleId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Rule> deleteRule(@PathVariable("ruleId") String ruleId,
			@RequestHeader("Accept-Language") String locale){
		
		if(!Strings.isNullOrEmpty(ruleId) && !Strings.isNullOrEmpty(locale)) {
			Rule rule = ruleRepository.find(ruleId);			
			if(rule != null) {
				ruleRepository.delete(rule);
				return new ResponseEntity<Rule>(rule, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale)),
				HttpStatus.NOT_FOUND);
		
	}

}
