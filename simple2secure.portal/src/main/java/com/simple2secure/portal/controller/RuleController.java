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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.CheckRegexNameDTO;
import com.simple2secure.api.dto.CheckRuleNameDTO;
import com.simple2secure.api.dto.ConditionExpressionDTO;
import com.simple2secure.api.dto.OsQueryReportDTO;
import com.simple2secure.api.dto.RegexTestDTO;
import com.simple2secure.api.dto.ConditionActionTemplatesDTO;
import com.simple2secure.api.dto.TemplateRulesDTO;
import com.simple2secure.api.dto.RuleMappingDTO;
import com.simple2secure.api.dto.RuleRegexDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.RuleDeviceMapping;
import com.simple2secure.api.model.RuleEmailConfigMapping;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleRegex;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.api.model.TemplateRuleFactTypePair;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputRuleFactType;
import com.simple2secure.portal.validation.model.ValidInputRuleId;
import com.simple2secure.portal.validation.model.ValidInputRuleRegex;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.RULE_API)
@Slf4j
public class RuleController extends BaseUtilsProvider {

	/**
	 *
	 * Method to save/update a rule with template action and condition into/from the database
	 *
	 * @param ruleFactType
	 *          type for which the rule was designed
	 * @param templateRule
	 *          object which should be saved/updated
	 * @param contextId
	 *          from where the message came from
	 * @param locale
	 *          which has been used in the web application
	 * @return ResponseEntity object with the templateRule object or an error.
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/templaterule",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> addOrUpdateTemplateRule(@PathVariable ValidInputRuleFactType ruleFactType,
			@RequestBody TemplateRule templateRule, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (templateRule != null) {

			// check if rule expression is valid
			if (ruleUtils.isConditionExpressionParsable(templateRule.getConditionExpression(),
					templateRule.getTemplateConditions().size()) == false) {
				log.error("Error condition expression of rule which should be saved/updated is not parsable.");
				throw new ApiRequestException(messageByLocaleService.getMessage("rules_error_condition_expression_not_valid", locale.getValue()));
			}

			ruleUtils.resetTextTags(templateRule);

			// check if there already exists a rule with the same name which is not the given
			TemplateRule foundRule = templateRuleRepository.findByContextIdAndRuleName(contextId.getValue(), templateRule.getName());

			if (templateRule.getId() == null) {
				// must be a new rule

				if (foundRule != null) {
					// there already exists a rule with the same name
					log.error("Error a user tried to save a new rule with a name which is already in usage.");
					throw new ApiRequestException(messageByLocaleService.getMessage("rule_name_already_used", locale.getValue()));
				}

				templateRule.setContextID(contextId.getValue());
				templateRule.id = templateRuleRepository.saveAndReturnId(templateRule);
				log.info("New rule saved id = {}", templateRule.getId());

				templateRuleFactTypeMappingRepository
						.save(new TemplateRuleFactTypePair(contextId.getValue(), RuleFactType.valueOf(ruleFactType.getValue()), templateRule.id));

			} else {
				// updating an already saved rule

				if ((foundRule != null) && !foundRule.getId().equals(templateRule.getId())) {
					// there already exists a rule with the same name which is not the same rule as provieded
					log.error("Error a user tried to change the name of a rule (ID = {}) to a name which is already in usage.", templateRule.getId());
					throw new ApiRequestException(messageByLocaleService.getMessage("rule_name_already_used", locale.getValue()));
				}

				templateRuleRepository.update(templateRule); 
				log.info("Updated rule ID = {}", templateRule.getId());
			}
			return new ResponseEntity<>(templateRule, HttpStatus.OK);
		}
		log.error("No rule data provided.");
		throw new ApiRequestException(messageByLocaleService.getMessage("rule_not_found", locale.getValue()));
	}

	/**
	 * Method to get rules by the fact type. Only as many rule as visible at one page in the table in the web.
	 * 
	 * @param contextId
	 * 					from the user which has send the request from the web
	 * @param ruleFactType
	 * 					the ruleFactType of the requested rules
	 * @param filter
	 * 					filter for the rules
	 * @param page
	 * 					which page will be displayed in the table in the web
	 * @param size
	 * 					which size one table have.
	 * @param locale
	 * 					which has been used in the web application
	 * @return
	 * 					ResponseEntity object with the RuleDTO objects or an error.
	 */
	@ValidRequestMapping(value = "/templaterules")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRulesDTO> getTemplateRuleByContextIdAndPaginationAndFactType(
			@ServerProvidedValue ValidInputContext contextId,
			@RequestParam(
		            required = true) String ruleFactType,
			@RequestParam(
		            required = false) String filter,
      @RequestParam(
                defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
      @RequestParam(
                defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {
		
		if (contextId.getValue() != null) {
			
			Context context = contextRepository.find(contextId.getValue());
			
			if (context != null) {
				
				log.debug("Loading TemplateRules for contextId {}", contextId.getValue());
				
				TemplateRulesDTO ruleDTO = new TemplateRulesDTO();
				
				ruleDTO = templateRuleRepository.findByContextIdAndFactTypeAndPagination(contextId.getValue(),  RuleFactType.valueOf(ruleFactType), page, size, filter);
				ruleDTO.getTemplateRules().forEach(rule -> ruleUtils.setLocaleTexts(rule, locale));
				
				return new ResponseEntity<>(ruleDTO, HttpStatus.OK);
			}
			
		}
		
		log.error("Error occured while retrieving rules for context {}", contextId);
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale.getValue()));
	}

	/**
	 *
	 * Method to get all condition/action templates by the fact type.
	 *
	 * @param ruleFactType
	 * @param contextId
	 * @param locale
	 * @return ResponseEntity object with the RuleConditionActionTemplatesDTO objects or an error.
	 */
	@ValidRequestMapping(
			value = "/template_conditions_actions")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")

	public ResponseEntity<ConditionActionTemplatesDTO> getConditionActionTemplates(@PathVariable ValidInputRuleFactType ruleFactType,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {
		ConditionActionTemplatesDTO dto = new ConditionActionTemplatesDTO();
		Collection<TemplateCondition> conditions;
		Collection<TemplateAction> actions;

		RuleFactType type = RuleFactType.valueOf(ruleFactType.getValue());

		// fetch conditions which are specific for this fact type
		conditions = ruleConditionsRepository.findConditionsByFactType(type);
		// add the general conditions which can be used for every fact type
		conditions.addAll(ruleConditionsRepository.findConditionsByFactType(RuleFactType.GENERAL));

		conditions.forEach(condition -> ruleUtils.setLocaleTexts(condition, locale));

		actions = ruleActionsRepository.findActionsByFactType(type);
		actions.addAll(ruleActionsRepository.findActionsByFactType(RuleFactType.GENERAL));

		actions.forEach(action -> ruleUtils.setLocaleTexts(action, locale));

		dto.setActions(actions);
		dto.setConditions(conditions);

		log.info("Fetched template conditions and actions for the rule engine to display in the web.");
		return new ResponseEntity<>(dto, HttpStatus.OK);
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
	@ValidRequestMapping(
			value = "/templaterule",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TemplateRule> deleteTemplateRule(@PathVariable ValidInputRuleId ruleId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {

		if (ruleId.getValue() != null) {
			TemplateRule templateRule = templateRuleRepository.find(ruleId.getValue());
			templateRuleFactTypeMappingRepository.deleteByRuleId(templateRule.getId());
			templateRuleRepository.delete(templateRule);
			return new ResponseEntity<>(templateRule, HttpStatus.OK);
		}
		log.error("Problem occured while deleting rule with ID = {}", ruleId.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_rule", locale.getValue()));

	}

	/**
	 *
	 * Method to save all objectsId from devices or configurations (depends on the given rule) to the given ruleId
	 *
	 * @param ruleMappingDTO
	 * @param contextId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/mapping",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleMappingDTO> saveRuleMappings(@RequestBody RuleMappingDTO ruleMappingDTO,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (ruleMappingDTO != null) {

			switch (ruleMappingDTO.getRuleFactType()) {
			case EMAIL: {
				ruleEmailConfigMappingRepository.deleteByRuleId(contextId.getValue(), ruleMappingDTO.getRuleId());
				ruleMappingDTO.getMappedObjectIds().forEach(emailConfigId -> {
					ruleEmailConfigMappingRepository
							.save(new RuleEmailConfigMapping(contextId.getValue(), ruleMappingDTO.getRuleId(), emailConfigId));
				});
				break;
			}

			case NETWORKREPORT:
			case OSQUERYREPORT:
			case TESTSEQUENCERESULT:
			case TESTRESULT: {
				ruleDeviceMappingRepository.deleteByRuleId(contextId.getValue(), ruleMappingDTO.getRuleId());
				ruleMappingDTO.getMappedObjectIds().forEach(devideId -> {
					ruleDeviceMappingRepository.save(new RuleDeviceMapping(contextId.getValue(), ruleMappingDTO.getRuleId(), devideId));
				});
				break;
			}

			default:
				break;

			}

			return new ResponseEntity<>(ruleMappingDTO, HttpStatus.OK);
		}
		log.error("Error no data provided for map a rule");
		throw new ApiRequestException(messageByLocaleService.getMessage("prbolem_occured_while_saving_rule_mapping", locale.getValue()));
	}

	/**
	 * Method to fetches all devicIds or configurationIds (depends on the given RuleFactType) which are mapped to the given ruleId.
	 *
	 * @param ruleFactType
	 * @param ruleId
	 * @param contextId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/mapping")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleMappingDTO> getMappedObjectIds(@PathVariable ValidInputRuleFactType ruleFactType,
			@PathVariable ValidInputRuleId ruleId, @ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		RuleFactType factType = RuleFactType.valueOf(ruleFactType.getValue());
		List<ObjectId> fetechedMappedObjectIds = new ArrayList<>();

		switch (factType) {
		case EMAIL: {
			List<RuleEmailConfigMapping> mappingPairs = ruleEmailConfigMappingRepository.getByContextIdAndRuleId(contextId.getValue(),
					ruleId.getValue());

			mappingPairs.forEach(pair -> {
				fetechedMappedObjectIds.add(pair.getEmailConfigurationId());
			});

			break;
		}

		case NETWORKREPORT:
		case OSQUERYREPORT:
		case TESTSEQUENCERESULT:
		case TESTRESULT: {
			List<RuleDeviceMapping> mappingPairs = ruleDeviceMappingRepository.getByContextIdAndRuleId(contextId.getValue(), ruleId.getValue());

			mappingPairs.forEach(pair -> {
				fetechedMappedObjectIds.add(pair.getDeviceId());
			});

			break;
		}

		default:
			break;

		}

		RuleMappingDTO ruleMappingDTO = new RuleMappingDTO();
		ruleMappingDTO.setContextId(contextId.getValue());
		ruleMappingDTO.setRuleFactType(factType);
		ruleMappingDTO.setRuleId(ruleId.getValue());
		ruleMappingDTO.setMappedObjectIds(fetechedMappedObjectIds);

		return new ResponseEntity<>(ruleMappingDTO, HttpStatus.OK);
	}

	/**
	 * Method to check if condition expression can be evaluated from the used library
	 *
	 * @param dto
	 *          ConditionExpressionDTO
	 * @param contextId
	 * @param locale
	 * @return ResponseEntity with the ConditionExpressionDTO and HttpStatus.OK => evaluable otherwise an error
	 */
	@ValidRequestMapping(
			value = "/check_condition_expression",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<ConditionExpressionDTO> checkConditionExpression(@RequestBody ConditionExpressionDTO dto,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {

		if (dto != null) {
			if (ruleUtils.isConditionExpressionParsable(dto.getExpression(), dto.getVariableCount()) == true) {
				String simplifiedExpression = ruleUtils.tryToSimplyConditionExpression(dto.getExpression());
				dto.setExpression(simplifiedExpression);
				return new ResponseEntity<>(dto, HttpStatus.OK);
			}

		}
		log.error("Error no data provided for checking a condition expression.");
		throw new ApiRequestException(messageByLocaleService.getMessage("rules_error_condition_expression_not_valid", locale.getValue()));
	}

	/**
	 * Method to check if the given rule name is already used
	 *
	 * @param dto
	 *          Where the name and the ruleId is encapsulated
	 * @param contextId
	 * @param locale
	 * @return ResponseEntity with Boolean false => no other rule has the same name, true => other rule has the same name
	 */
	@ValidRequestMapping(
			value = "/check_name_used",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Boolean> checkRuleNameUsed(@RequestBody CheckRuleNameDTO dto, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(dto.getName())) {

			TemplateRule foundRule = templateRuleRepository.findByContextIdAndRuleName(contextId.getValue(), dto.getName());

			if (foundRule == null) {
				// no rule with the same name found
				return new ResponseEntity<>(new Boolean(false), HttpStatus.OK);
			} else {
				if (foundRule.getId().equals(new ObjectId(dto.getRuleId()))) {
					// the found rule is the same as the rule which is now updated by the user
					return new ResponseEntity<>(new Boolean(false), HttpStatus.OK);

				} else {
					// found rule with the same name
					return new ResponseEntity<>(new Boolean(true), HttpStatus.OK);
				}
			}
		}
		
		log.error("Error no data provided for checking if a name for a rule is already in use.");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_evaluating_rulename", locale.getValue()));
	}

	/**
	 * Method to save/update a regex into/from the database
	 *
	 * @param ruleRegex
	 *          object which should be saved
	 * @param contextId
	 * @param locale
	 *          which has been used in the web application
	 * @return ResponseEntity object with the ruleRegex object or an error.
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/regex",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleRegex> addOrUpdateRuleRegex(@RequestBody RuleRegex ruleRegex, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (ruleRegex != null) {
			RuleRegex foundRegex = ruleRegexRepository.findByContextIdAndName(contextId.getValue(), ruleRegex.getName());

			if (ruleRegex.getId() != null) {
				// check if there already exists a regex with the same name which is not the given
				if (foundRegex != null && !foundRegex.getId().equals(ruleRegex.getId())) {
					log.error("Error user tried to update a rule regex with a name which is already used.");
					throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_evaluating_regexname", locale.getValue()));
				}

				ruleRegexRepository.update(ruleRegex);

			} else {
				// check if there already exists a regex with the same name
				if (foundRegex != null) {
					log.error("Error user tried to save a new rule regex with a name which is already in use.");
					throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_evaluating_regexname", locale.getValue()));
				}
				if (ruleRegex.getContextId() == null) {
					ruleRegex.setContextId(contextId.getValue());
				}
				ruleRegexRepository.save(ruleRegex);
			}
			return new ResponseEntity<>(ruleRegex, HttpStatus.OK);
		}
		log.error("Error no data provided for saving a rule regex.");
		throw new ApiRequestException(messageByLocaleService.getMessage("regex_not_found", locale.getValue()));
	}

	/**
	 *
	 * Method to delete a RuleRegex object.
	 *
	 * @param ruleRegexId
	 *          of the rule which should be deleted
	 * @param contextId
	 * @param locale
	 *          which has been used in the web application
	 * @return ResponseEntity object with the deleted ruleRegex or an error.
	 */
	@ValidRequestMapping(
			value = "/regex",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleRegex> deleteRuleRegex(@PathVariable ValidInputRuleRegex ruleRegexId,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {

		if (ruleRegexId.getValue() != null) {
			RuleRegex ruleRegex = ruleRegexRepository.find(ruleRegexId.getValue());
			if (ruleRegex != null) {
				ruleRegexRepository.delete(ruleRegex);
				return new ResponseEntity<>(ruleRegex, HttpStatus.OK);
			}
		}
		log.error("Error no rule regex ID provided for deleting the data.");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_regex", locale.getValue()));
	}


	
	/**
	 * Method to get all saved regexes for the rules
	 * 
	 * @param contextId
	 * 						from the user which has send the request from the web
	 * @param filter
	 * 					filter for the rules
	 * @param page
	 * 					which page will be displayed in the table in the web
	 * @param size
	 * 					which size one table have.
	 * @param locale
	 * 						which has been used in the web application
	 * @return ResponseEntity object with the RuleRegexDTO objects
	 */
	@ValidRequestMapping(
			value = "/regex")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<RuleRegexDTO> getRuleRegexDTOByContxtId(
			@ServerProvidedValue ValidInputContext contextId,
			@RequestParam(
		            required = false) String filter,
      @RequestParam(
                defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
      @RequestParam(
                defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null) {
			
			Context context = contextRepository.find(contextId.getValue());
			
			if (context != null) {
				
				log.debug("Loading RuleRegex for contextId {}", contextId.getValue());
				
				RuleRegexDTO ruleRegexDTO = ruleRegexRepository.findByContextIdAndPagination(contextId.getValue(), page, size, filter);
				
				return new ResponseEntity<>(ruleRegexDTO, HttpStatus.OK);				
			}
		}
		log.error("Error occured while retrieving rule regex for context {}");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_regexes", locale.getValue()));
	}

	/**
	 *
	 * Method to test if the given text matches with the given regex
	 *
	 * @param regexTestDTO
	 *          object which which will be tested
	 * @param contextId
	 * @param locale
	 *          which has been used in the web application
	 * @return ResponseEntity object with a boolean: true = regex matches the text, false otherwise
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/regex/test",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Boolean> testRexes(@RequestBody RegexTestDTO regexTestDTO, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (regexTestDTO != null) {
			boolean matches = regexTestDTO.getTestText().matches(regexTestDTO.getRegex());
			return new ResponseEntity<>(matches, HttpStatus.OK);
		}
		log.error("Error no contextId provided.");
		throw new ApiRequestException(messageByLocaleService.getMessage("no_data_provieded", locale.getValue()));
	}

	/**
	 *
	 * Method to check if the given regex name is already used
	 *
	 * @param dto
	 *          CheckRegexNameDTO
	 * @param contextId
	 * @param locale
	 * @return ResponseEntity object with the Boolean: true => is used, false => not used by an other regex object
	 */
	@ValidRequestMapping(
			value = "/regex/check_name_used",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Boolean> checkRegexNameUsed(@RequestBody CheckRegexNameDTO dto, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(dto.getName())) {
			RuleRegex regex = ruleRegexRepository.findByContextIdAndName(contextId.getValue(), dto.getName());

			if (regex == null) {
				// name not used yet
				return new ResponseEntity<>(new Boolean(false), HttpStatus.OK);
			} else {
				if (regex.getId().equals(new ObjectId(dto.getRegexId()))) {
					// name found but its the same object
					return new ResponseEntity<>(new Boolean(false), HttpStatus.OK);
				} else {
					// other regex with same name found
					return new ResponseEntity<>(new Boolean(true), HttpStatus.OK);
				}
			}
		}
		log.error("Error no data provided for checking if a regex name is already used.");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_evaluating_regexname", locale.getValue()));
	}
	
	 
}
