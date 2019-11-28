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
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.Service;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DeviceUtils;
import com.simple2secure.portal.utils.TestUtils;

import simple2secure.validator.annotation.ValidInput;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputHostname;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSize;

@RestController
@RequestMapping(StaticConfigItems.DEVICE_API)
public class DeviceController {

	public static final Logger log = LoggerFactory.getLogger(DeviceController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	TestUtils testUtils;

	/**
	 * This function returns all pods according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getPodsByContextId(@ValidInput ValidInputContext contextId, @PathVariable ValidInputPage page,
			@PathVariable ValidInputSize size, @ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				Map<String, Object> pods = deviceUtils.getAllDevicesFromCurrentContextPagination(context, page.getValue(), size.getValue());

				if (pods != null) {
					return new ResponseEntity<>(pods, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving pods for contextId {}", contextId.getValue());

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all pods according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@RequestMapping(value = "/{contextId}/{active}")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Device>> getPodsByContextIdAndStatus(@ValidInput ValidInputContext contextId, @PathVariable boolean active,
			@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<Device> pods = deviceUtils.getAllDevicesFromCurrentContext(context, active);

				if (pods != null) {
					return new ResponseEntity<>(pods, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving pods for contextId {}", contextId);

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function retrieves the tests according to the deviceId, if no tests are found then it checks if there are tests according to the
	 * hostname.
	 *
	 * @param deviceId
	 * @param hostname
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(value = "/config")
	public ResponseEntity<List<Test>> checkConfiguration(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputHostname hostname)
			throws ItemNotFoundRepositoryException {

		List<Test> test = testRepository.getByDeviceId(deviceId.getValue());
		CompanyLicensePrivate podLicense = licenseRepository.findByDeviceId(deviceId.getValue());

		if (podLicense != null) {
			podLicense.setLastOnlineTimestamp(System.currentTimeMillis());
			licenseRepository.update(podLicense);
		}

		if (test == null || test.isEmpty()) {
			test = testRepository.getByHostname(hostname.getValue());
		}

		return new ResponseEntity<>(test, HttpStatus.OK);

	}

	/**
	 * This function retrieves the scheduled tests from the list of the scheduled tests and returns them to the pod.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(value = "/scheduledTests", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<TestRun>> getScheduledTests(@PathVariable ValidInputDevice deviceId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());

		if (license != null) {
			license.setLastOnlineTimestamp(System.currentTimeMillis());
			licenseRepository.update(license);
			return testUtils.getScheduledTestsByDeviceId(deviceId.getValue(), locale.getValue());
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_tests", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function deletes the specified the POD with the specified ID if it exists
	 */
	@ValidRequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> deletePod(@PathVariable ValidInputDevice deviceId, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(deviceId.getValue())) {

			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());

			if (license != null) {
				// delete all device dependencies
				deviceUtils.deleteDependencies(deviceId.getValue());
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}

		log.error("Problem occured while deleting device with id {}", deviceId);
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_device", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all devices according to the user id
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(value = "/changeGroup", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> changeGroupProbe(@PathVariable ValidInputDevice deviceId, @RequestBody CompanyGroup group,
			@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(deviceId.getValue()) && group != null) {
			// retrieve license from database
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
			CompanyGroup dbGroup = groupRepository.find(group.getId());
			if (license != null && dbGroup != null) {

				license.setGroupId(dbGroup.getId());
				// TODO - check what needs to be updated in order that probe gets a correct
				// during the next license check
				licenseRepository.update(license);
				return new ResponseEntity<>(license, HttpStatus.OK);
			}
		}

		log.error("Problem occured while updating group for device id {}", deviceId.getValue());

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_device_group", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/status")
	public ResponseEntity<Service> getStatus(@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		Service currentVersion = new Service("simple2secure", loadedConfigItems.getVersion());
		currentVersion.setId("1");
		return new ResponseEntity<>(currentVersion, HttpStatus.OK);
	}

	@ValidRequestMapping(value = "/status", method = RequestMethod.POST)
	public ResponseEntity<Service> postStatus(@PathVariable ValidInputDevice deviceId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
			if (license != null) {
				license.setLastOnlineTimestamp(System.currentTimeMillis());
				licenseRepository.update(license);
			}
		}
		return new ResponseEntity<>(new Service("simple2secure", loadedConfigItems.getVersion()), HttpStatus.OK);
	}

}
