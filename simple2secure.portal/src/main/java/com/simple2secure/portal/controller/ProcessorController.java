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
	@RequestMapping(
			value = "",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Processor> saveOrUpdateProcessor(@RequestBody Processor processor, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		// TODO - implement a method to check it the processor with the provided id exists in the update case and check if probe or group id are
		// empty!!!

		if (processor != null) {
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
			return new ResponseEntity<>(processor, HttpStatus.OK);
		}
		log.error("Error occured while saving/updating processor");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_processor", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/{probeId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	public ResponseEntity<List<Processor>> getProcessorsByProbeId(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(probeId)) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(probeId);
			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());
				if (group != null) {
					List<Processor> processors = new ArrayList<>();
					if (group.isRootGroup()) {
						// This is root group, get only processors for this group
						processors = repository.getProcessorsByGroupId(license.getGroupId());
						if (processors != null) {
							return new ResponseEntity<>(processors, HttpStatus.OK);
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

						return new ResponseEntity<>(processors, HttpStatus.OK);
					}
				}

			}
		}
		log.error("Error while retrieving processors for probe with id {}", probeId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/group/{groupId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Processor>> getProcessorsByGroupId(@PathVariable("groupId") String groupId,
			@RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(groupId)) {
			List<Processor> processors = repository.getProcessorsByGroupId(groupId);
			if (processors != null) {
				return new ResponseEntity<>(processors, HttpStatus.OK);
			}
		}
		log.error("Error while retrieving processors for group with id {}", groupId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(
			value = "/{processorId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteProcessor(@PathVariable("processorId") String processorId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(processorId)) {
			Processor processor = repository.find(processorId);
			if (processor != null) {
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
		log.error("Error occured while deleting processor with id {}", processorId);
		return new ResponseEntity<>(
				new CustomErrorType(
						messageByLocaleService.getMessage("problem_occured_while_deleting_processor", ObjectUtils.toObjectArray(processorId), locale)),
				HttpStatus.NOT_FOUND);

	}
}
