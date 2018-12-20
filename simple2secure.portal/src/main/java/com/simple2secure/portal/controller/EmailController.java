/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

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
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.MailUtils;

@RestController
@RequestMapping("/api/email")
public class EmailController {

	static final Logger log = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private EmailConfigurationRepository emailConfigRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	MailUtils mailUtils;

	public static final Logger logger = LoggerFactory.getLogger(EmailController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<EmailConfiguration> saveEmailConfiguration(@RequestBody EmailConfiguration config,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if (config != null) {
			if (!mailUtils.checkIfEmailConfigExists(config.getEmail(), config.getContextId())) {
				if (!Strings.isNullOrEmpty(config.getId())) {
					emailConfigRepository.update(config);
				} else {
					emailConfigRepository.save(config);
				}
				return new ResponseEntity<EmailConfiguration>(config, HttpStatus.OK);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<EmailConfiguration>> getEmailConfigByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<EmailConfiguration> emailConfigurationList = emailConfigRepository.findByContextId(context.getId());
				if (emailConfigurationList != null) {
					return new ResponseEntity<List<EmailConfiguration>>(emailConfigurationList, HttpStatus.OK);
				}
			}
		}

		log.error("Error occured while getting email config for user with id {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_email_config", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/inbox/{emailConfigId}", method = RequestMethod.GET)
	public ResponseEntity<List<Email>> getEmailMessagesByConfigId(@PathVariable("emailConfigId") String emailConfigId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(emailConfigId)) {
			EmailConfiguration emailConfig = emailConfigRepository.find(emailConfigId);
			if (emailConfig != null) {
				List<Email> emails = emailRepository.findByConfigId(emailConfigId);
				if (emails != null) {
					return new ResponseEntity<List<Email>>(emails, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while getting email messages for email configuration with id {}", emailConfigId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_email_config", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function deletes configuration and user according to the user id
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/{emailConfigId}", method = RequestMethod.DELETE)
	public ResponseEntity<EmailConfiguration> deleteEmailConfig(@PathVariable("emailConfigId") String emailConfigId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(emailConfigId)) {
			EmailConfiguration emailConfig = emailConfigRepository.find(emailConfigId);

			if (emailConfig != null) {
				mailUtils.deleteEmailConfiguration(emailConfig);
				return new ResponseEntity<EmailConfiguration>(emailConfig, HttpStatus.OK);

			}
		}
		log.error("Error occured while deleting email configuration with id {}", emailConfigId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_email_config", locale)),
				HttpStatus.NOT_FOUND);
	}
}