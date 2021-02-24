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

import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple2secure.api.dbo.GenericDBObject;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.rules.engine.GeneralRulesEngineImpl;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.RuleUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * Specific implementation of the GeneralRulesEngine for the portal.
 *
 * This engine supports the following objects as input: Email, OsQueryReport, NetworkReport, TestResult, TestSequenceResult;
 *
 */
@Service
@Slf4j
public class PortalRuleEngine extends GeneralRulesEngineImpl {
	@Autowired
	private MessageByLocaleService messageByLocaleService;

	@Autowired
	private RuleUtils ruleUtils;

	@Autowired
	private TestRunRepository testRunRepository;

	/**
	 * Method to fetch all rule data from the database which are mapped to the type of factObject.
	 *
	 * @param <T>
	 *          generic type which must be derived from the base class GenericDBObject.
	 *
	 * @param factObject
	 *          The Object which will be used as input for the rules.
	 * @param contextId
	 *          The context id from where the factObject has been collected.
	 */
	private <T extends GenericDBObject> void registerRules(T factObject, ObjectId contextId) {
		// fetch the mapped enum element
		RuleFactType ruleFactType = RuleFactType.getEnumFromObject(factObject);
		if (ruleFactType == null) {
			// when the ruleFactType is null, the object type is not supported yet
			log.debug("The given object is not supported in the portal rule engine. Classname: {}", factObject.getClass());
			throw new InconsistentDataException(new Message("error_rule_engine_not_supported_fact",
					messageByLocaleService.getMessage("error_rule_engine_not_supported_fact", "en")));
		}

		Set<TemplateRule> templateRules = new TreeSet<>();

		// fetching the rule data for the given object type
		switch (ruleFactType) {
		case EMAIL:
			Email email = (Email) factObject;
			templateRules = ruleUtils.getEmailTemplateRules(contextId, email.getConfigId());
			break;
		case NETWORKREPORT: {
			NetworkReport report = (NetworkReport) factObject;
			templateRules = ruleUtils.getTemplateRules(contextId, report.getDeviceId());
			break;
		}
		case OSQUERYREPORT: {
			OsQueryReport report = (OsQueryReport) factObject;
			templateRules = ruleUtils.getTemplateRules(contextId, report.getDeviceId());
			break;
		}
		case TESTRESULT: {
			TestResult result = (TestResult) factObject;
			TestRun testRun = testRunRepository.find(result.getTestRunId());
			templateRules = ruleUtils.getTemplateRules(contextId, testRun.getPodId());
			break;
		}
		case TESTSEQUENCERESULT: {
			TestSequenceResult result = (TestSequenceResult) factObject;
			templateRules = ruleUtils.getTemplateRules(contextId, result.getPodId());
			break;
		}

		default: {
			throw new InconsistentDataException(new Message("error_rule_engine_not_supported_fact",
					messageByLocaleService.getMessage("error_rule_engine_not_supported_fact", "en")));
		}

		}

		// building and saving the actual rule objects which can be used in the engine
		Set<Rule> rules = ruleUtils.buildRulesFromTemplateRulesWithBeanAndLimit(templateRules,
				StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH, StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);

		addRules(rules);
	}

	/**
	 * Method to check the given input with the rule engine and the rules which are designed for the given factObject mapped by the user.
	 *
	 * @param <T>
	 *          generic type which must be derived from the base class GenericDBObject.
	 * @param factObject
	 *          The Object which will be used as input for the rules.
	 * @param contextId
	 *          The context id from where the factObject has been collected.
	 */
	public <T extends GenericDBObject> void check(T factObject, ObjectId contextId) {
		addFact(factObject);
		addFact(contextId);

		registerRules(factObject, contextId);

		//checkFacts();
		log.info("Checked a {} Object with ID = {}",  factObject.getClass(), factObject.getId());

		removeFact(factObject.getClass().getName());
		removeFact(contextId.getClass().getName());
		removeAllRules();
	}

}
