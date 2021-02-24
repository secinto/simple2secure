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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.bson.types.ObjectId;
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
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.ReportType;
import com.simple2secure.api.model.Service;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputDeviceType;
import com.simple2secure.portal.validation.model.ValidInputGroup;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.NotSecuredApi;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.DEVICE_API)
@Slf4j
public class DeviceController extends BaseUtilsProvider {

	/**
	 * This function returns all devices according to the contextId and type with merged test objects
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getDevicesByContextIdAndType(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputDeviceType deviceType, @RequestParam(
					required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@RequestParam(
					defaultValue = "false") boolean active,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (contextId.getValue() != null && !Strings.isNullOrEmpty(deviceType.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				Map<String, Object> devices = deviceUtils.getAllDevicesFromContextWithPaginationByType(contextId.getValue(), true,
						deviceType.getValue(), size, page, locale.getValue(), filter);

				if (devices != null) {
					return new ResponseEntity<>(devices, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving devices for contextId {} and deviceType {}", contextId.getValue(), deviceType.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue()));
	}

	/**
	 * This function returns all devices according to the contextId and type with merged test objects
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping("/group")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getDevicesByGroupId(@PathVariable ValidInputGroup groupId, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (groupId.getValue() != null) {

			Map<String, Object> devices = deviceUtils.getAllDevicesByGroupId(groupId.getValue(), page, size, filter);

			return new ResponseEntity<>(devices, HttpStatus.OK);

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue()));
	}

	/**
	 * This function returns all devices according to the contextId and type with merged test objects
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/test")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getDevicesByContextIdAndType(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputDeviceType deviceType, @ServerProvidedValue ValidInputUser user, @RequestParam(
					required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@RequestParam(
					required=false) boolean isPublic,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (contextId.getValue() != null && !Strings.isNullOrEmpty(deviceType.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				Map<String, Object> devices = new HashMap();
				if (isPublic) {
					devices = deviceUtils.getAllPublicPodDevicesPagination(page, size, filter);
				}else {
					devices = deviceUtils.getAllDevicesByIdAndTypeWithMergedTestObjects(context, deviceType.getValue(),
							user.getValue(), page, size, filter);
				}

				if (devices != null) {
					return new ResponseEntity<>(devices, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving devices for contextId {} and deviceType {}", contextId.getValue(), deviceType.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/visibility",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<Device> updatePodVisibility(@RequestBody Device device, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputContext context, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (device != null) {
			ContextUserAuthentication cUA = contextUserAuthRepository.getByContextIdAndUserId(device.getGroup().getContextId(), user.getValue());
			if (userUtils.checkIsUserAdminInContext(cUA)) {
				deviceUtils.updatePodVisibility(device);
				DeviceInfo devInfoFromDB = deviceInfoRepository.findByDeviceId(device.getInfo().getId());

				return new ResponseEntity<>(new Device(device.getGroup(), devInfoFromDB), HttpStatus.OK);
			} else {
				throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_has_no_rights", locale.getValue()));
			}
		} else {
			throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_user_devices", locale.getValue()));
		}
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
	public ResponseEntity<List<Device>> getDevicesByGroupId(@RequestBody List<CompanyGroup> groups, @RequestParam ReportType reportType,
			@RequestParam DeviceType deviceType, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (groups != null) {

			List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
			List<Device> devices = deviceUtils.getAllDevicesWithReportsByGroupId(groupIds, deviceType, reportType);

			return new ResponseEntity<>(devices, HttpStatus.OK);

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_user_devices", locale.getValue()));
	}

	/**
	 * This function returns all devices according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Device>> getDevicesByContextIdAndStatus(@ServerProvidedValue ValidInputContext contextId,
			@RequestParam boolean active, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (contextId.getValue() != null) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, active);

				if (devices != null) {
					return new ResponseEntity<>(devices, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving pods for contextId {}", contextId);
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale.getValue()));
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
    @PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
    public ResponseEntity<List<TestRun>> getScheduledTests(@PathVariable ValidInputDevice deviceId,
            @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
        DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());
        if (devInfo != null) {
            devInfo.setLastOnlineTimestamp(System.currentTimeMillis());
            deviceInfoRepository.update(devInfo);
            return testUtils.getScheduledTestsByDeviceId(deviceId.getValue(), locale);
        }
        throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_tests", locale.getValue()));
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

		if (deviceId.getValue() != null && group != null) {
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
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_device_group", locale.getValue()));
	}

	@NotSecuredApi
	@ValidRequestMapping(
			value = "/status")
	public ResponseEntity<Service> getStatus(@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		Service currentVersion = new Service("simple2secure", loadedConfigItems.getVersion());
		currentVersion.setId(new ObjectId("1"));
		return new ResponseEntity<>(currentVersion, HttpStatus.OK);
	}

	@ValidRequestMapping(
			value = "/status",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<Service> postStatus(@PathVariable ValidInputDevice deviceId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (deviceId.getValue() != null) {
			DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());
			if (devInfo != null) {
				devInfo.setLastOnlineTimestamp(System.currentTimeMillis());
				deviceInfoRepository.update(devInfo);
			}
		}
		return new ResponseEntity<>(new Service("simple2secure", loadedConfigItems.getVersion()), HttpStatus.OK);
	}

	@ValidRequestMapping(
			value = "/update",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<DeviceInfo> updateDeviceInfo(@RequestBody DeviceInfo deviceInfo, @ServerProvidedValue ValidInputLocale locale,
			@ServerProvidedValue ValidInputUser userId) throws ItemNotFoundRepositoryException {
		DeviceInfo deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getId());

		if (deviceInfo != null && deviceInfoFromDB == null) {
			/*
			 * TODO: Verify if this still works 
			 */
			deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
			if (deviceInfo.getType().equals(DeviceType.POD)) {
				deviceInfo.setPubliclyAvailable(true);
			}
			deviceInfoRepository.save(deviceInfo);
			deviceInfoFromDB = deviceInfoRepository.findByDeviceId(deviceInfo.getId());
		} else if (deviceInfo != null && deviceInfoFromDB != null) {
			deviceInfoFromDB.setDeviceStatus(deviceInfo.getDeviceStatus());
			deviceInfoFromDB.setLastOnlineTimestamp(System.currentTimeMillis());
			deviceInfoRepository.update(deviceInfoFromDB);
		}
		return new ResponseEntity<>(deviceInfoFromDB, HttpStatus.OK);
	}
}
