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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Step;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class StepController {

	@Autowired
	private StepRepository repository;
	
    @Autowired
    MessageByLocaleService messageByLocaleService;	

	public static final Logger logger = LoggerFactory.getLogger(StepController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/steps/{probeId}/{select_all}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	public ResponseEntity<List<Step>> getStepsByProbeId(@PathVariable("probeId") String probeId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {
		List<Step> steps = this.repository.getStepsByProbeId(probeId, select_all);
		if(steps == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_steps", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Step>>(steps, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/steps/group/{groupId}/{select_all}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Step>> getStepsByGroupId(@PathVariable("groupId") String groupId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {
		List<Step> steps = this.repository.getStepsByGroupId(groupId, select_all, true);
		if(steps == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_steps", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Step>>(steps, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/steps", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Step> saveOrUpdateStep(@RequestBody Step step, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		
		//TODO - implement a method to check it the step with the provided id exists in the update case and check if probe or group id are empty!!!
		if(Strings.isNullOrEmpty(step.getId())) {
			
			//Set the correct number
			if(!Strings.isNullOrEmpty(step.getGroupId())) {
				List<Step> steps = this.repository.getStepsByGroupId(step.getGroupId(), true, true);
				step.setGroupStep(true);
				step.setNumber(steps.size() + 1);
			}
			else {
				List<Step> steps = this.repository.getStepsByProbeId(step.getProbeId(), true);
				step.setGroupStep(false);
				step.setNumber(steps.size() + 1);
			}
						
			this.repository.save(step);
		}
		else {
			
			this.repository.update(step);
		}
				
		return new ResponseEntity<Step>(step, HttpStatus.OK);
	}
	
	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/api/steps/{stepId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteProcessor(@PathVariable("stepId") String stepId, @RequestHeader("Accept-Language") String locale) {
		Step step = this.repository.find(stepId);
		if (step == null) {
			return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_step",
					ObjectUtils.toObjectArray(stepId), locale)), HttpStatus.NOT_FOUND);
		} else {
			this.repository.delete(step);
			return new ResponseEntity<>(step, HttpStatus.OK);
		}
	}	
}