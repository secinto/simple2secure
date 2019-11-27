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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.EmailConfigurationDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.api.model.validation.ValidInputContext;
import com.simple2secure.api.model.validation.ValidInputEmailConfig;
import com.simple2secure.api.model.validation.ValidInputLocale;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.validator.ValidInput;
import com.simple2secure.portal.validator.ValidRequestMapping;

@RestController
@RequestMapping(StaticConfigItems.EMAIL_API)
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

	@ValidRequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<EmailConfiguration> saveEmailConfiguration(@RequestBody EmailConfiguration config,
			@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (config != null) {
			String configId = mailUtils.checkIfEmailConfigExists(config);
			if (!Strings.isNullOrEmpty(configId)) {
				config.setId(configId);
				emailConfigRepository.update(config);
			} else {
				emailConfigRepository.save(config);
			}
			return new ResponseEntity<>(config, HttpStatus.OK);
		}

		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping()
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<EmailConfigurationDTO>> getEmailConfigByContextId(@ValidInput ValidInputContext contextId,
			@ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<EmailConfigurationDTO> emailConfigurationList = mailUtils.getEmailConfigDTO(contextId.getValue());
				if (emailConfigurationList != null) {
					return new ResponseEntity<>(emailConfigurationList, HttpStatus.OK);
				}
			}
		}

		log.error("Error occured while getting email config for user with id {}", contextId.getValue());
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_email_config", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function deletes configuration and user according to the user id
	 *
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@ValidRequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<EmailConfiguration> deleteEmailConfig(@PathVariable ValidInputEmailConfig emailConfigId,
			@ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(emailConfigId.getValue())) {
			EmailConfiguration emailConfig = emailConfigRepository.find(emailConfigId.getValue());

			if (emailConfig != null) {
				mailUtils.deleteEmailConfiguration(emailConfig);
				return new ResponseEntity<>(emailConfig, HttpStatus.OK);

			}
		}
		log.error("Error occured while deleting email configuration with id {}", emailConfigId);
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_email_config", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}