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
import com.simple2secure.api.model.FrontendRule;
import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.RuleRepository;
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
	public ResponseEntity<FrontendRule> addOrUpdateRule(@RequestBody FrontendRule rule, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (rule != null) {

			PortalRule portalRule = ruleUtils.convertFrontendRuleToPortalRule(rule);

			if (portalRule != null) {
				if (!Strings.isNullOrEmpty(rule.getId())) {
					portalRule.setId(rule.getId());
					ruleRepository.update(portalRule);
				} else {
					ruleRepository.save(portalRule);
				}
			}

			return new ResponseEntity<>(rule, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{toolId}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<FrontendRule>> getEmailMessagesByUserID(@PathVariable("toolId") String toolId,
			@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(toolId) && !Strings.isNullOrEmpty(contextId)) {

			List<FrontendRule> frontRules = ruleUtils.getFrontendRulesByToolId(toolId);

			if (frontRules != null) {
				return new ResponseEntity<>(frontRules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<FrontendRule>> getRulesByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {

			List<FrontendRule> frontRules = ruleUtils.getFrontendRulesByContextId(contextId);

			if (frontRules != null) {
				return new ResponseEntity<>(frontRules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}

}
