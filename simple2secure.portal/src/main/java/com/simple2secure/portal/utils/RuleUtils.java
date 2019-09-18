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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.ExtendedRule;
import com.simple2secure.api.model.FrontendRule;
import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.repository.RuleRepository;

@Configuration
@Component
public class RuleUtils {

	@Autowired
	RuleRepository ruleRepository;

	/**
	 * This function retrieves portal rules by the toolId from db and converts portal rules to the frontend rules.
	 *
	 * @param toolId
	 * @return
	 */
	public List<FrontendRule> getFrontendRulesByToolId(String toolId) {

		List<FrontendRule> rules = new ArrayList<>();

		List<PortalRule> portalRules = ruleRepository.findByToolId(toolId);

		if (portalRules != null) {
			for (PortalRule portalRule : portalRules) {
				if (portalRule != null) {
					rules.add(convertPortalRuleToFrontend(portalRule));
				}
			}
		}

		return rules;

	}

	/**
	 * This function retrieves all rules by the contextId from the database and converts them to the fronted rules
	 *
	 * @param contextId
	 * @return
	 */
	public List<FrontendRule> getFrontendRulesByContextId(String contextId) {
		List<FrontendRule> rules = new ArrayList<>();

		List<PortalRule> portalRules = ruleRepository.findByContextId(contextId);

		if (portalRules != null) {
			for (PortalRule portalRule : portalRules) {
				if (portalRule != null) {
					rules.add(convertPortalRuleToFrontend(portalRule));
				}
			}
		}

		return rules;
	}

	/**
	 * This function converts a portal rule object to the frontend rule object
	 *
	 * @param rule
	 * @return
	 */
	private FrontendRule convertPortalRuleToFrontend(PortalRule rule) {
		FrontendRule fr = new FrontendRule(rule.getToolId(), rule.getContextId(), rule.getClazz(), rule.getName(), rule.getDescription(),
				rule.getPriority(), rule.getTimestamp(), rule.isActive());
		return fr;
	}

	/**
	 * This function converts the Frontend rule to the portal rule
	 *
	 * @param rule
	 * @return
	 */
	public PortalRule convertFrontendRuleToPortalRule(FrontendRule rule) {
		PortalRule pr = new PortalRule(rule.getToolId(), rule.getContextId(), rule.getClazz(), rule.getName(), rule.getDescription(),
				rule.getPriority(), createExtendedRuleFromFrontendRule(rule), System.currentTimeMillis(), rule.isActive());
		return pr;

	}

	/**
	 * This function converts frontend rule to the Extended rule
	 *
	 * TODO: hardcoded parts must be changed to work also for another tools
	 *
	 * @param rule
	 * @return
	 */
	public ExtendedRule createExtendedRuleFromFrontendRule(FrontendRule rule) {
		ExtendedRule extRule = new ExtendedRule(rule.getName(), "input.subject == 'test'", "notificationAction", rule.getPriority(),
				rule.getClazz(), rule.getDescription());

		return extRule;
	}
}