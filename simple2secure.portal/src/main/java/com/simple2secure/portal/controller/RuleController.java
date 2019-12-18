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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputRule;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

/**
 *
 * @author Richard Heinz
 *
 *         In this class all request for the rules between web and server are implemented
 *
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping(StaticConfigItems.RULE_API)
public class RuleController extends BaseUtilsProvider {

	private static Logger log = LoggerFactory.getLogger(RuleController.class);

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

	@ValidRequestMapping(value = "/rulewithsource", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> addOrUpdateRuleWithSourcecode(@RequestBody RuleWithSourcecode ruleWithSourcecode,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (ruleWithSourcecode != null) {

			if (!Strings.isNullOrEmpty(ruleWithSourcecode.getId())) {
				ruleWithSourcecodeRepository.update(ruleWithSourcecode);
			} else {
				if (Strings.isNullOrEmpty(ruleWithSourcecode.getContextID())) {
					ruleWithSourcecode.setContextID(contextId.getValue());
				}
				ruleWithSourcecodeRepository.save(ruleWithSourcecode);
			}

			return new ResponseEntity<>(ruleWithSourcecode, HttpStatus.OK);

		}
		return ((ResponseEntity<RuleWithSourcecode>) buildResponseEntity("rule_not_found", locale));
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
	@ValidRequestMapping(value = "/templaterule", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> addOrUpdateTemplateRule(@RequestBody TemplateRule templateRule,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (templateRule != null) {

			if (!Strings.isNullOrEmpty(templateRule.getId())) {
				templateRuleRepository.update(templateRule);
			} else {
				if (Strings.isNullOrEmpty(templateRule.getContextID())) {
					templateRule.setContextID(contextId.getValue());
				}
				templateRuleRepository.save(templateRule);
			}

			return new ResponseEntity<>(templateRule, HttpStatus.OK);

		}
		return ((ResponseEntity<TemplateRule>) buildResponseEntity("rule_not_found", locale));

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
	@ValidRequestMapping(value = "/templaterule")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TemplateRule>> getTemplateRulesByContxtId(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {

			List<TemplateRule> templateRules = ruleUtils.getTemplateRulesByContextId(contextId.getValue());

			if (templateRules != null && templateRules.size() > 0) {
				return new ResponseEntity<>(templateRules, HttpStatus.OK);
			}
		}

		return (ResponseEntity<List<TemplateRule>>) buildResponseEntity("problem_occured_while_getting_rules", locale);
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
	@ValidRequestMapping(value = "/rulewithsource")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<RuleWithSourcecode>> getRulesWithSourcecodeByContextId(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {

			List<RuleWithSourcecode> ruleWithSourcecodes = ruleUtils.getRuleWithSourcecodeRepositoryByContextId(contextId.getValue());

			if (ruleWithSourcecodes != null) {
				return new ResponseEntity<>(ruleWithSourcecodes, HttpStatus.OK);
			}
		}

		return (ResponseEntity<List<RuleWithSourcecode>>) buildResponseEntity("problem_occured_while_getting_rules", locale);
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
	@ValidRequestMapping(value = "/template_conditions")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateCondition>> getTemplateConditions(String rule_templates,
			@ServerProvidedValue ValidInputLocale locale) {
		Collection<TemplateCondition> conditions;
		try {
			conditions = ruleConditionsRepository.findAll();
			if (conditions == null) {
				conditions = ruleUtils.loadTemplateConditions("com.simple2secure.portal.rules.conditions");
				conditions.forEach(ruleConditionsRepository::save);
			}

		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage());
			return (ResponseEntity<Collection<TemplateCondition>>) buildResponseEntity("problem_occured_while_getting_rules", locale);
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
	@ValidRequestMapping(value = "/template_actions")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Collection<TemplateAction>> getTemplateActions(String rule_templates,
			@ServerProvidedValue ValidInputLocale locale) {
		Collection<TemplateAction> actions;
		try {
			actions = ruleActionsRepository.findAll();
			if (actions == null) {
				actions = ruleUtils.loadTemplateActions("com.simple2secure.portal.rules.actions");
				actions.forEach(ruleActionsRepository::save);
			}

		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage());
			return (ResponseEntity<Collection<TemplateAction>>) buildResponseEntity("problem_occured_while_getting_rules", locale);
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
	@ValidRequestMapping(value = "/rulewithsource", method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleWithSourcecode> deleteRuleWithSourcecode(@PathVariable ValidInputRule ruleId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(ruleId.getValue()) && !Strings.isNullOrEmpty(locale.getValue())) {
			RuleWithSourcecode ruleWithSourcecode = ruleWithSourcecodeRepository.find(ruleId.getValue());
			if (ruleWithSourcecode != null) {
				ruleWithSourcecodeRepository.delete(ruleWithSourcecode);
				return new ResponseEntity<>(ruleWithSourcecode, HttpStatus.OK);
			}
		}

		return (ResponseEntity<RuleWithSourcecode>) buildResponseEntity("problem_occured_while_deleting_rule", locale);

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
	@ValidRequestMapping(value = "/templaterule", method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> deleteTemplateRule(@PathVariable ValidInputRule ruleId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(ruleId.getValue()) && !Strings.isNullOrEmpty(locale.getValue())) {
			TemplateRule templateRule = templateRuleRepository.find(ruleId.getValue());
			if (templateRule != null) {
				templateRuleRepository.delete(templateRule);
				return new ResponseEntity<>(templateRule, HttpStatus.OK);
			}
		}

		return (ResponseEntity<TemplateRule>) buildResponseEntity("problem_occured_while_deleting_rule", locale);

	}

}
