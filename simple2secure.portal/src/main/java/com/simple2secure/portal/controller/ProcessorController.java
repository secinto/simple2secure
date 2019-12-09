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

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'DEVICE')")
	public ResponseEntity<List<Processor>> getProcessors(@ServerProvidedValue ValidInputLocale locale) {
		List<Processor> processors = repository.findAll();
		if (processors != null) {
			return new ResponseEntity<>(processors, HttpStatus.OK);
		}
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Processor> saveOrUpdateProcessor(@RequestBody Processor processor, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (processor != null) {
			if (Strings.isNullOrEmpty(processor.getId())) {
				List<Processor> processors = repository.findAll();

				if (portalUtils.checkIfListAlreadyContainsProcessor(processors, processor)) {
					return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("processor_already_exist", locale.getValue())),
							HttpStatus.NOT_FOUND);
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

				Step step = stepRepository.getByName(processor.getName());

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
