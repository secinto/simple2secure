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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Step;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputProcessor;

@RestController
@RequestMapping(StaticConfigItems.PROCESSOR_API)
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

	@ValidRequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Processor> saveOrUpdateProcessor(@RequestBody Processor processor, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		// TODO - implement a method to check it the processor with the provided id exists in the update case and check if probe or group id are
		// empty!!!

		if (processor != null) {
			if (Strings.isNullOrEmpty(processor.getId())) {
				if (!Strings.isNullOrEmpty(processor.getGroupId())) {
					List<Processor> processors = repository.getProcessorsByGroupId(processor.getGroupId());

					if (portalUtils.checkIfListAlreadyContainsProcessor(processors, processor)) {
						return new ResponseEntity<>(
								new CustomErrorType(messageByLocaleService.getMessage("processor_already_exist", locale.getValue())), HttpStatus.NOT_FOUND);
					}
				}
				repository.save(processor);
			} else {
				repository.update(processor);
			}
			return new ResponseEntity<>(processor, HttpStatus.OK);
		}
		log.error("Error occured while saving/updating processor");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_processor", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'DEVICE')")
	public ResponseEntity<List<Processor>> getProcessorsByDeviceId(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
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
							return new ResponseEntity<>(
									new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale.getValue())),
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
		log.error("Error while retrieving processors for probe with id {}", deviceId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/group")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Processor>> getProcessorsByGroupId(@PathVariable ValidInputGroup groupId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(groupId.getValue())) {
			List<Processor> processors = repository.getProcessorsByGroupId(groupId.getValue());
			if (processors != null) {
				return new ResponseEntity<>(processors, HttpStatus.OK);
			}
		}
		log.error("Error while retrieving processors for group with id {}", groupId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@ValidRequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteProcessor(@PathVariable ValidInputProcessor processorId, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(processorId.getValue())) {
			Processor processor = repository.find(processorId.getValue());
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
		log.error("Error occured while deleting processor with id {}", processorId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_processor",
				ObjectUtils.toObjectArray(processorId), locale.getValue())), HttpStatus.NOT_FOUND);

	}
}
