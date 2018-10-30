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
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.Step;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
public class ProcessorController {

	static final Logger log = LoggerFactory.getLogger(ProcessorController.class);

	@Autowired
	ProcessorRepository repository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/processors/{probeId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	public ResponseEntity<List<Processor>> getProcessorsByProbeId(@PathVariable("probeId") String probeId,
			@RequestHeader("Accept-Language") String locale) {
		List<Processor> processors = repository.getProcessorsByProbeId(probeId);
		if (processors == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_processors", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Processor>>(processors, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/processors/group/{groupId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Processor>> getProcessorsByGroupId(@PathVariable("groupId") String groupId,
			@RequestHeader("Accept-Language") String locale) {
		List<Processor> processors = repository.getProcessorsByGroupId(groupId, true);
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
				List<Processor> processors = repository.getProcessorsByGroupId(processor.getGroupId(), true);

				if (portalUtils.checkIfListAlreadyContainsProcessor(processors, processor)) {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("processor_already_exist", locale)),
							HttpStatus.NOT_FOUND);
				}

				processor.setGroupProcessor(true);
			} else {
				List<Processor> processors = repository.getProcessorsByProbeId(processor.getProbeId());

				if (portalUtils.checkIfListAlreadyContainsProcessor(processors, processor)) {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("processor_already_exist", locale)),
							HttpStatus.NOT_FOUND);
				}
				processor.setGroupProcessor(false);
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
	@RequestMapping(value = "/api/processors/{processorId}", method = RequestMethod.DELETE)
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

			if (processor.isGroupProcessor()) {
				if (!Strings.isNullOrEmpty(processor.getGroupId())) {
					step = stepRepository.getByNameAndGroupId(processor.getName(), processor.getGroupId());
				}
			} else {
				if (!Strings.isNullOrEmpty(processor.getProbeId())) {
					step = stepRepository.getByNameAndProbeId(processor.getName(), processor.getProbeId());
				}
			}

			if (step != null) {
				stepRepository.delete(step);
			}

			repository.delete(processor);

			return new ResponseEntity<>(processor, HttpStatus.OK);
		}
	}

	/**
	 * This function updates the current probe configuration from the current group configuration
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	@RequestMapping(value = "/api/processors/update/group", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Probe> applyGroupStepsAndProcessors(@RequestBody Probe probe, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (probe != null) {
			if (!Strings.isNullOrEmpty(probe.getProbeId())) {
				CompanyLicense license = licenseRepository.findByProbeId(probe.getProbeId());

				if (license != null) {
					if (!Strings.isNullOrEmpty(license.getGroupId())) {
						// Delete probe processors and steps
						List<Processor> probeProcessors = repository.getProcessorsByProbeId(probe.getProbeId());

						if (probeProcessors != null) {
							for (Processor processor : probeProcessors) {
								repository.delete(processor);
							}
						} else {
							log.debug("No processors found for this probe");
						}

						List<Step> probeSteps = stepRepository.getStepsByProbeId(probe.getProbeId(), true);

						if (probeSteps != null) {
							for (Step step : probeSteps) {
								stepRepository.delete(step);
							}
						} else {
							log.debug("No steps found for this probe");
						}

						// Copy group processors and steps

						List<Processor> groupProcessors = repository.getProcessorsByGroupId(license.getGroupId(), true);
						if (groupProcessors != null) {
							for (Processor processor : groupProcessors) {
								processor.setId(null);
								processor.setProbeId(probe.getProbeId());
								processor.setGroupProcessor(false);
								repository.save(processor);
							}
						} else {
							log.debug("No processors found for this group!");
						}

						List<Step> groupSteps = stepRepository.getStepsByGroupId(license.getGroupId(), true, true);

						if (groupSteps != null) {
							for (Step step : groupSteps) {
								step.setId(null);
								step.setProbeId(probe.getProbeId());
								step.setGroupStep(false);
								stepRepository.save(step);
							}
						} else {
							log.debug("No steps found for this group!");
						}

						return new ResponseEntity<Probe>(probe, HttpStatus.OK);

					} else {
						log.error("Group id not found in the license object");
						return new ResponseEntity(
								new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_steps_processors", locale)),
								HttpStatus.NOT_FOUND);
					}
				} else {
					log.error("License for the provided probeId does not exist!");
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_steps_processors", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				log.error("ProbeId cannot be null or empty");
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_steps_processors", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			log.error("Probe object cannot be null");
			return new ResponseEntity(
					new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_steps_processors", locale)),
					HttpStatus.NOT_FOUND);
		}
	}
}
