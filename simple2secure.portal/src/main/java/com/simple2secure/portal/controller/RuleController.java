/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.simple2secure.api.model.ExtendedRule;
import com.simple2secure.api.model.FrontendRule;
import com.simple2secure.api.model.PortalRule;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<FrontendRule> addOrUpdateRule(@RequestBody FrontendRule rule, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (rule != null) {

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String dateTime = dateFormat.format(date);
			ExtendedRule r1 = new ExtendedRule(rule.getName(), "input.subject == 'test'", "notificationAction", rule.getPriority(),
					"com.simple2secure.api.model.Email", rule.getDescription());

			PortalRule portalRule = new PortalRule(rule.getToolId(), rule.getContextId(), r1, dateTime, true);
			if (!Strings.isNullOrEmpty(rule.getId())) {
				portalRule.setId(rule.getId());
				ruleRepository.update(portalRule);
			} else {
				ruleRepository.save(portalRule);
			}

			return new ResponseEntity<FrontendRule>(rule, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("rule_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{toolId}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<FrontendRule>> getEmailMessagesByUserID(@PathVariable("toolId") String toolId,
			@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(toolId) && !Strings.isNullOrEmpty(contextId)) {
			List<PortalRule> rules = ruleRepository.findByToolAndContextId(toolId, contextId);

			if (rules != null) {
				List<FrontendRule> frontRules = new ArrayList<>();
				for (PortalRule rule : rules) {
					FrontendRule fr = new FrontendRule(rule.getId(), rule.getToolId(), rule.getContextId(), rule.getRule().getName(),
							rule.getRule().getDescription(), rule.getRule().getPriority(), rule.getCreatedOn(), rule.isActive());
					frontRules.add(fr);
				}
				return new ResponseEntity<List<FrontendRule>>(frontRules, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_rules", locale)),
				HttpStatus.NOT_FOUND);
	}
}
