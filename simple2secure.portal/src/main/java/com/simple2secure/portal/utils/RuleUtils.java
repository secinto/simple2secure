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
	 * This function retrieves portal rules from db and converts portal rules to the frontend rules.
	 *
	 * @param toolId
	 * @return
	 */
	public List<FrontendRule> getFrontendRulesByToolId(String toolId) {

		List<FrontendRule> rules = new ArrayList<FrontendRule>();

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
