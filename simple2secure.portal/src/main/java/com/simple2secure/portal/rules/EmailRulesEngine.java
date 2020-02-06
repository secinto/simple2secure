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

import java.util.ArrayList;
import java.util.List;

import org.jeasy.rules.api.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.RuleUserPair;
import com.simple2secure.api.model.RuleWithSourcecode;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.rules.engine.GeneralRulesEngineImpl;
import com.simple2secure.portal.repository.RuleUserPairsRepository;
import com.simple2secure.portal.utils.RuleUtils;

/**
 *
 * @author Richard Heinz
 *
 *         A rule engine for checking mails. Extents the GeneralRulesEngineImpl.
 *
 */
@Service
public class EmailRulesEngine extends GeneralRulesEngineImpl {

	@Autowired
	private RuleUtils ruleUtils;
	
	@Autowired
	private RuleUserPairsRepository ruleUserPairRepository;

	private static Logger log = LoggerFactory.getLogger(EmailRulesEngine.class);

	/**
	 * Method to load, create and register all rules which are given for this user.
	 *
	 * @param contextId
	 *          of the user
	 */
	private void registerRules(String contextId, Email email) {
		List<RuleWithSourcecode> ruleWithSourcecodes = ruleUtils.getRuleWithSourcecodeRepositoryByContextId(contextId);

		ruleWithSourcecodes.forEach(rule -> {
			try {
				Object newRule = ruleUtils.createRuleFromSourceWithBean(rule.getSourcecode());
				addRule(newRule);

			} catch (Exception e) {
				log.debug("Unable to load Rule " + rule.getName() + " " + e.getMessage());
			}
		});

		log.debug("Created and registered expert rules with sourcecode");

		List<RuleUserPair> ruleUserPairs = ruleUserPairRepository.getByContextIdAndEmailConfigId(contextId, email.getConfigId());
		List<TemplateRule> templateRules = new ArrayList<TemplateRule>();
		
		ruleUserPairs.forEach(pair -> {
			templateRules.add(ruleUtils.getTemplateRulesByContextIdAndRuleId(contextId, pair.getRuleId()));
		});
		
		templateRules.forEach(ruleInfo -> {
			try {
				Rule ruleObj = ruleUtils.buildRuleFromTemplateRuleWithBean(ruleInfo, "com.simple2secure.portal.rules.conditions",
						"com.simple2secure.portal.rules.actions");

				addRule(ruleObj);

			} catch (Exception e) {
				log.error("Unable to load Rule " + ruleInfo.getName() + " " + e.getMessage());
			}
		});

		log.debug("Created and registered predefined template rules");
	}

	/**
	 * Method to check an email with the given rules.
	 *
	 * @param email
	 *          which should be checked with the given rules
	 * @param contextId
	 *          of the logged in user in the web
	 */
	public void checkMail(Email email, String contextId) {
		addFact(email);
		registerRules(contextId, email);
		checkFacts();
		removeFact(email.getClass().getName());
		rules_.clear();
	}
}
