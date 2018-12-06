/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.ArrayList;
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
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Step;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
@RequestMapping("/api/processors")
public class ProcessorController {

	static final Logger log = LoggerFactory.getLogger(ProcessorController.class);

	@Autowired
	ProcessorRepository repository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{probeId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	public ResponseEntity<List<Processor>> getProcessorsByProbeId(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {

		CompanyLicensePrivate license = licenseRepository.findByProbeId(probeId);

		if (license != null) {
			CompanyGroup group = groupRepository.find(license.getGroupId());

			if (group != null) {
				List<Processor> processors = new ArrayList<>();
				if (group.isRootGroup()) {
					// This is root group, get only processors for this group
					processors = repository.getProcessorsByGroupId(license.getGroupId());
					if (processors != null) {
						return new ResponseEntity<List<Processor>>(processors, HttpStatus.OK);
					} else {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
								HttpStatus.NOT_FOUND);
					}
				} else {
					// This is not root group get all processors from all parent groups, until we find the root group
					List<CompanyGroup> foundGroups = portalUtils.findAllParentGroups(group);

					// Iterate through all found groups and add their queries to the queryConfig
					for (CompanyGroup cg : foundGroups) {
						List<Processor> currentProcessors = repository.getProcessorsByGroupId(cg.getId());
						if (currentProcessors != null) {
							processors.addAll(currentProcessors);
						}
					}

					return new ResponseEntity<List<Processor>>(processors, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/group/{groupId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Processor>> getProcessorsByGroupId(@PathVariable("groupId") String groupId,
			@RequestHeader("Accept-Language") String locale) {
		List<Processor> processors = repository.getProcessorsByGroupId(groupId);
		if (processors == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Processor>>(processors, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/processors", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Processor> saveOrUpdateProcessor(@RequestBody Processor processor, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		// TODO - implement a method to check it the processor with the provided id exists in the update case and check if probe or group id are
		// empty!!!

		if (Strings.isNullOrEmpty(processor.getId())) {
			if (!Strings.isNullOrEmpty(processor.getGroupId())) {
				List<Processor> processors = repository.getProcessorsByGroupId(processor.getGroupId());

				if (portalUtils.checkIfListAlreadyContainsProcessor(processors, processor)) {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("processor_already_exist", locale)),
							HttpStatus.NOT_FOUND);
				}
			}
			repository.save(processor);
		} else {
			repository.update(processor);
		}
		return new ResponseEntity<Processor>(processor, HttpStatus.OK);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/{processorId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteProcessor(@PathVariable("processorId") String processorId,
			@RequestHeader("Accept-Language") String locale) {
		Processor processor = repository.find(processorId);
		if (processor == null) {
			return new ResponseEntity<>(new CustomErrorType(
					messageByLocaleService.getMessage("problem_occured_while_deleting_processor", ObjectUtils.toObjectArray(processorId), locale)),
					HttpStatus.NOT_FOUND);
		} else {

			// Check according to the processor name if the same step exists
			Step step = null;

			if (!Strings.isNullOrEmpty(processor.getGroupId())) {
				step = stepRepository.getByNameAndGroupId(processor.getName(), processor.getGroupId());
			}

			if (step != null) {
				stepRepository.delete(step);
			}

			repository.delete(processor);

			return new ResponseEntity<>(processor, HttpStatus.OK);
		}
	}
}
