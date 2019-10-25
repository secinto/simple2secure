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

package com.simple2secure.portal.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.simple2secure.portal.repository.RuleActionsRepository;
import com.simple2secure.portal.repository.RuleConditionsRepository;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.RuleUtils;

/**
 *
 * @author Richard Heinz
 *
 *         In this class all request for the rules between web and server are implemented
 *
 */
@RestController
@RequestMapping("/api/rule")
public class RuleController {

	private static Logger log = LoggerFactory.getLogger(RuleController.class);

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

	/**
	 * Method to save/update the source code for a rule into/from the database
	 *
	 * @param ruleWithSourcecode
	 *          object which should be saved into the database
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the ruleWithSourcecode object or with an error.
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/rulewithsource/", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> addOrUpdateRuleWithSourcecode(@RequestBody RuleWithSourcecode ruleWithSourcecode,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (ruleWithSourcecode != null) {

			if (!Strings.isNullOrEmpty(ruleWithSourcecode.getId())) {
				ruleWithSourcecodeRepository.update(ruleWithSourcecode);
			} else {
				ruleWithSourcecodeRepository.save(ruleWithSourcecode);
			}

			return new ResponseEntity<>(ruleWithSourcecode, HttpStatus.OK);

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	/**
	 * Method to save/update a rule with template action and condition into/from the database
	 *
	 * @param templateRule
	 *          object which should be saved
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the templateRule object or an error.
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/templaterule/", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> addOrUpdateTemplateRule(@RequestBody TemplateRule templateRule,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (templateRule != null) {

			if (!Strings.isNullOrEmpty(templateRule.getId())) {
				templateRuleRepository.update(templateRule);
			} else {
				templateRuleRepository.save(templateRule);
			}

			return new ResponseEntity<>(templateRule, HttpStatus.OK);

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	/**
	 * Method to get all rules which are build with template actions and conditions from the database
	 *
	 * @param contextId
	 *          from the user which has send the request from the web
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the TemplateRule objects as a List or an error.
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/templaterule/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TemplateRule>> getTemplateRulesByContxtId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<TemplateRule> templateRules = ruleUtils.getTemplateRulesByContextId(contextId);

			if (templateRules != null) {
				return new ResponseEntity<>(templateRules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Method to get all free defined rules from the database
	 *
	 * @param contextId
	 *          from the user which has send the request from the web
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the rules in a List or an error.
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/rulewithsource/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<RuleWithSourcecode>> getRulesWithSourcecodeByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<RuleWithSourcecode> ruleWithSourcecodes = ruleUtils.getRuleWithSourcecodeRepositoryByContextId(contextId);

			if (ruleWithSourcecodes != null) {
				return new ResponseEntity<>(ruleWithSourcecodes, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * Method to get all predefined Conditions. If they are none saved in the database they will be searched.
	 *
	 * @param rule_templates
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the Conditions in a List or an error.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/template_conditions/", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateCondition>> getTemplateConditions(String rule_templates,
			@RequestHeader("Accept-Language") String locale) {
		Collection<TemplateCondition> conditions;
		try {
			conditions = ruleConditionsRepository.findAll();
			if (conditions == null) {
				conditions = ruleUtils.loadTemplateConditions("com.simple2secure.portal.rules.conditions");
				conditions.forEach(ruleConditionsRepository::save);
			}

		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity(new CustomErrorType("Failed to load predefined contitions"), HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<>(conditions, HttpStatus.OK);
	}

	/**
	 * Method to get all predefined Actions. If they are none saved in the database they will be searched.
	 *
	 * @param rule_templates
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the Actions in a List or an error.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/template_actions/", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateAction>> getTemplateActions(String rule_templates,
			@RequestHeader("Accept-Language") String locale) {
		Collection<TemplateAction> actions;
		try {
			actions = ruleActionsRepository.findAll();
			if (actions == null) {
				actions = ruleUtils.loadTemplateActions("com.simple2secure.portal.rules.actions");
				actions.forEach(ruleActionsRepository::save);
			}

		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage());
			return new ResponseEntity(new CustomErrorType("Failed to load predefined contitions"), HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<>(actions, HttpStatus.OK);
	}

	/**
	 * Method to delete free defined rule.
	 *
	 * @param ruleId
	 *          of the rule which should be deleted
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the deleted rule or an error.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/rulewithsource/{ruleId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> deleteRuleWithSourcecode(@PathVariable("ruleId") String ruleId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(ruleId) && !Strings.isNullOrEmpty(locale)) {
			RuleWithSourcecode ruleWithSourcecode = ruleWithSourcecodeRepository.find(ruleId);
			if (ruleWithSourcecode != null) {
				ruleWithSourcecodeRepository.delete(ruleWithSourcecode);
				return new ResponseEntity<>(ruleWithSourcecode, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * Method to delete a TemplateRule object.
	 *
	 * @param ruleId
	 *          of the rule which should be deleted
	 * @param locale
	 *          which has been used in the web application
	 *
	 * @return ResponseEntity object with the deleted rule or an error.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/templaterule/{ruleId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> deleteTemplateRule(@PathVariable("ruleId") String ruleId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(ruleId) && !Strings.isNullOrEmpty(locale)) {
			TemplateRule templateRule = templateRuleRepository.find(ruleId);
			if (templateRule != null) {
				templateRuleRepository.delete(templateRule);
				return new ResponseEntity<>(templateRule, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale)),
				HttpStatus.NOT_FOUND);

	}

}
