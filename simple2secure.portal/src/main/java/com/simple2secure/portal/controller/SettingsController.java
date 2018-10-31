/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
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
import com.simple2secure.api.dto.SettingsDTO;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.Settings;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class SettingsController {
	
	@Autowired
	SettingsRepository settingsRepository;
	
	@Autowired
	LicensePlanRepository licensePlanRepository;
		
    @Autowired
    MessageByLocaleService messageByLocaleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/settings", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<SettingsDTO> getSettings(@RequestHeader("Accept-Language") String locale) {
		List<Settings> settings = settingsRepository.findAll();
		List<LicensePlan> licensePlans = licensePlanRepository.findAll();
		
		if(settings != null) {
			if(settings.size() == 1) {
				return new ResponseEntity<SettingsDTO>(new SettingsDTO(settings.get(0), licensePlans), HttpStatus.OK);
			}
			else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_settings", locale)), HttpStatus.NOT_FOUND);
			}			
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_settings", locale)), HttpStatus.NOT_FOUND);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/settings", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<Settings> updateSettings(@RequestBody Settings settings, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if(settings != null) {
			settingsRepository.update(settings);
			return new ResponseEntity<Settings>(settings, HttpStatus.OK);
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale)), HttpStatus.NOT_FOUND);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/settings/licensePlan", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<LicensePlan> saveLicensePlan(@RequestBody LicensePlan licensePlan, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if(licensePlan != null) {
			if(Strings.isNullOrEmpty(licensePlan.getId())) {
				licensePlanRepository.save(licensePlan);
			}
			else {
				licensePlanRepository.update(licensePlan);
			}
			return new ResponseEntity<LicensePlan>(licensePlan, HttpStatus.OK);
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_settings", locale)), HttpStatus.NOT_FOUND);
		}
	}	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/settings/licensePlan/{licensePlanId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('SUPERADMIN')")
	public ResponseEntity<LicensePlan> deleteLicensePlan(@PathVariable("licensePlanId") String licensePlanId, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		LicensePlan licensePlan = licensePlanRepository.find(licensePlanId);
		if(licensePlan != null) {
			licensePlanRepository.delete(licensePlan);
			return new ResponseEntity<LicensePlan>(licensePlan, HttpStatus.OK);
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_license_plan", locale)), HttpStatus.NOT_FOUND);
		}
	}
}
