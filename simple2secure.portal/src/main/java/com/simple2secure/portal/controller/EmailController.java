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

import com.simple2secure.api.model.Email;
import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class EmailController {
	
	static final Logger log = LoggerFactory.getLogger(EmailController.class);
		
	@Autowired
	private EmailConfigurationRepository emailConfigRepository;
	
	@Autowired 
	EmailRepository emailRepository;
	
    @Autowired
    MessageByLocaleService messageByLocaleService;	

	public static final Logger logger = LoggerFactory.getLogger(EmailController.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/email", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<EmailConfiguration> saveEmailConfiguration(@RequestBody EmailConfiguration config, @RequestHeader("Accept-Language") String locale){
		if(config != null) {
			emailConfigRepository.save(config);
			return new ResponseEntity<EmailConfiguration>(config, HttpStatus.OK);
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);		
		}		
	}
	
	@RequestMapping(value = "/api/email/{user_id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<EmailConfiguration>> getEmailConfigByUserID(@PathVariable("user_id") String user_id, @RequestHeader("Accept-Language") String locale){
		return new ResponseEntity<List<EmailConfiguration>>(emailConfigRepository.findByUserUUID(user_id), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/api/email/inbox/{email_config_id}", method = RequestMethod.GET)
	public ResponseEntity<List<Email>> getEmailMessagesByUserID(@PathVariable("email_config_id") String email_config_id, @RequestHeader("Accept-Language") String locale) {
			
		EmailConfiguration config = emailConfigRepository.findByConfigId(email_config_id);
		
		if(config != null) {				
			//extractEmailsFromMessages(connect(config), config.getUserUUID(), email_config_id);
			List<Email> emails = emailRepository.findByUserUUIDAndConfigID(config.getUserUUID(), email_config_id);
			return new ResponseEntity<List<Email>>(emails, HttpStatus.OK);
										
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_email_config", locale)), HttpStatus.NOT_FOUND);
		}
	
	}
	
	/**
	 * This function deletes configuration and user according to the user id
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/api/email/{config_id}", method = RequestMethod.DELETE)
	public ResponseEntity<EmailConfiguration> deleteConfig(@PathVariable("config_id") String configId, @RequestHeader("Accept-Language") String locale) {
		EmailConfiguration config = this.emailConfigRepository.deleteByConfigId(configId);
		if(config == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_email_config", locale)), HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<EmailConfiguration>(config, HttpStatus.OK);	
		}
				
	}	
}