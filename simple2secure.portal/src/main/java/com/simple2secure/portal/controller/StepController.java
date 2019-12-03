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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Step;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputStep;

@RestController
@RequestMapping(StaticConfigItems.STEP_API)
public class StepController {

	@Autowired
	private StepRepository repository;

	@Autowired
	private LicenseRepository licenseRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	public static final Logger log = LoggerFactory.getLogger(StepController.class);

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'DEVICE')")
	public ResponseEntity<List<Step>> getStepsByDeviceId(@PathVariable ValidInputDevice deviceId, @RequestParam boolean select_all,
			@ServerProvidedValue ValidInputLocale locale) {
		log.debug("Retrieving steps for probe id {}", deviceId.getValue());
		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());

			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());

				if (group != null) {
					List<Step> steps = new ArrayList<>();
					if (group.isRootGroup()) {
						// This is root group get configuration from this group only
						steps = repository.getStepsByGroupId(license.getGroupId(), select_all);

					} else {
						// This is not root group get all processors from all parent groups, until we find the root group
						List<CompanyGroup> foundGroups = portalUtils.findAllParentGroups(group);
						// Iterate through all found groups and add their queries to the queryConfig
						for (CompanyGroup cg : foundGroups) {
							List<Step> currentSteps = repository.getStepsByGroupId(cg.getId(), select_all);
							if (currentSteps != null) {
								steps.addAll(currentSteps);
							}
						}
					}
					if (steps != null) {
						return new ResponseEntity<>(steps, HttpStatus.OK);
					}
				}
			}
		}
		log.error("Error while retrieving steps for probe id {}", deviceId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_steps", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/group")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Step>> getStepsByGroupId(@PathVariable ValidInputGroup groupId, @RequestParam boolean select_all,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(groupId.getValue())) {
			List<Step> steps = repository.getStepsByGroupId(groupId.getValue(), select_all);
			if (steps != null) {
				return new ResponseEntity<>(steps, HttpStatus.OK);
			}
		}
		log.error("Error while retrieving steps for group id {}", groupId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_steps", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Step> saveOrUpdateStep(@RequestBody Step step, @ServerProvidedValue ValidInputLocale locale,
			@ServerProvidedValue ValidInputContext context) throws ItemNotFoundRepositoryException {

		if (step != null) {
			if (Strings.isNullOrEmpty(step.getId())) {

				// Set the correct number
				if (!Strings.isNullOrEmpty(step.getGroupId())) {
					List<Step> steps = repository.getStepsByGroupId(step.getGroupId(), true);
					step.setNumber(steps.size() + 1);
				}
				repository.save(step);
			} else {
				repository.update(step);
			}

			return new ResponseEntity<>(step, HttpStatus.OK);
		}
		log.error("Error while updating step");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("error_while_saving_step", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteStep(@PathVariable ValidInputStep stepId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(stepId.getValue())) {
			Step step = repository.find(stepId.getValue());
			List<Step> steps = repository.getAllGreaterThanNumber(step.getNumber(), step.getGroupId());
			{
				repository.delete(step);
				if (steps != null) {
					for (Step stepObj : steps) {
						stepObj.setNumber(stepObj.getNumber() - 1);
						repository.update(stepObj);
					}
				}
				return new ResponseEntity<>(step, HttpStatus.OK);
			}
		}
		log.error("Error while deleting step with id {}", stepId.getValue());
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_step", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}