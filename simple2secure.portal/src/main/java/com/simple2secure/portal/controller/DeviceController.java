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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.Service;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputHostname;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSize;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.DEVICE_API)
public class DeviceController extends BaseUtilsProvider {

	public static final Logger log = LoggerFactory.getLogger(DeviceController.class);

	/**
	 * This function returns all devices according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getDevicesByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				Map<String, Object> pods = deviceUtils.getAllDevicesFromCurrentContextPagination(context, page.getValue(), size.getValue());

				if (pods != null) {
					return new ResponseEntity<>(pods, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving devices for contextId {}", contextId.getValue());

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_devices", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}
	
	
	/**
	 * This function returns all pods according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(value = "/pods")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getPodsByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				Map<String, Object> pods = deviceUtils.getAllPodsFromCurrentContextPagination(context, page.getValue(), size.getValue());

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
	@ValidRequestMapping(
			value = "/group",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Device>> getPodsByGroupId(@RequestBody List<CompanyGroup> groups, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (groups != null) {
			List<String> groupIds = portalUtils.extractIdsFromObjects(groups);

			if (groupIds != null) {
				List<Device> devices = deviceUtils.getAllProbesByGroupIds(groupIds, false);
				if (devices != null) {
					return new ResponseEntity<>(devices, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_user_devices", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all pods according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Device>> getPodsByContextIdAndStatus(@ServerProvidedValue ValidInputContext contextId,
			@RequestParam boolean active, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

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
	@ValidRequestMapping(
			value = "/config")
	public ResponseEntity<List<Test>> checkConfiguration(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputHostname hostname)
			throws ItemNotFoundRepositoryException {

		List<Test> test = testRepository.getByDeviceId(deviceId.getValue());
		DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());
		if (devInfo != null) {
			devInfo.setLastOnlineTimestamp(System.currentTimeMillis());
			deviceInfoRepository.update(devInfo);
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
	@ValidRequestMapping(
			value = "/scheduledTests",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<TestRun>> getScheduledTests(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());
		if (devInfo != null) {
			devInfo.setLastOnlineTimestamp(System.currentTimeMillis());
			deviceInfoRepository.update(devInfo);
			return testUtils.getScheduledTestsByDeviceId(deviceId.getValue(), locale.getValue());
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_tests", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function deletes the specified the POD with the specified ID if it exists
	 */
	@ValidRequestMapping(
			value = "/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> deletePod(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {

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
	@ValidRequestMapping(
			value = "/changeGroup",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<CompanyLicensePrivate> changeGroupProbe(@PathVariable ValidInputDevice deviceId, @RequestBody CompanyGroup group,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

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

	@ValidRequestMapping(
			value = "/status")
	public ResponseEntity<Service> getStatus(@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		Service currentVersion = new Service("simple2secure", loadedConfigItems.getVersion());
		currentVersion.setId("1");
		return new ResponseEntity<>(currentVersion, HttpStatus.OK);
	}

	@ValidRequestMapping(
			value = "/status",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<Service> postStatus(@PathVariable ValidInputDevice deviceId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(deviceId.getValue())) {
			DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());
			if (devInfo != null) {
				devInfo.setLastOnlineTimestamp(System.currentTimeMillis());
				deviceInfoRepository.update(devInfo);
				sutUtils.updateSUTLastOnlineTtimestamp(devInfo.getDeviceId(), System.currentTimeMillis());
			}
		}
		return new ResponseEntity<>(new Service("simple2secure", loadedConfigItems.getVersion()), HttpStatus.OK);
	}

	@ValidRequestMapping(
			value = "/update",
			method = ValidRequestMethodType.POST)
	public ResponseEntity<DeviceInfo> updateDeviceInfo(@RequestBody DeviceInfo deviceInfo, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		DeviceInfo deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getDeviceId());
		CompanyLicensePublic license = licenseRepository.findByDeviceId(deviceInfo.getDeviceId());
		if (deviceInfo != null && deviceInfoFromDB == null) {
			if (!license.isDevicePod()) {
				deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
				deviceInfoRepository.save(deviceInfo);
				CompanyGroup group = groupRepository.find(license.getGroupId());
				sutUtils.addProbeAsSUT(license, group.getContextId());
				deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getDeviceId());
			} else {
				deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
				deviceInfoRepository.save(deviceInfo);
				deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getDeviceId());
			}
		} else if (deviceInfo != null && deviceInfoFromDB != null) {
			deviceInfoFromDB.setLastOnlineTimestamp(System.currentTimeMillis());
			deviceInfoRepository.update(deviceInfoFromDB);
			deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getDeviceId());
			sutUtils.updateSUTLastOnlineTtimestamp(deviceInfoFromDB.getDeviceId(), System.currentTimeMillis());
		}
		return new ResponseEntity<>(deviceInfoFromDB, HttpStatus.OK);
	}
}
