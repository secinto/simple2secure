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

package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeviceUtils extends BaseServiceProvider {

	@Autowired
	TestUtils testUtils;

	@Autowired
	PortalUtils portalUtils;

	/**
	 * This function returns all probes from the current context
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException 
	 */
	public List<Device> getAllDevicesFromCurrentContext(Context context, boolean active) throws ItemNotFoundRepositoryException {
		log.debug("Retrieving devices for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<Device> devices = new ArrayList<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findAllByGroupId(group.getId());
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (!Strings.isNullOrEmpty(license.getDeviceId())) {
						DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());
						if (devInfo != null) {
							DeviceStatus status = devInfo.getDeviceStatus();

							DeviceStatus deviceStatus = getDeviceStatus(devInfo);
							if (status != deviceStatus) {
								devInfo.setDeviceStatus(deviceStatus);
								deviceInfoRepository.update(devInfo);
							}

							Device device = new Device(group, devInfo);
							devices.add(device);
						}
					}
				}
			}
		}
		log.debug("Retrieved {} probes for context {}", devices.size(), context.getName());
		return devices;
	}

	/**
	 * This function creates the devices objects from the CompanyLicensePrivate objects and returns it according to the provided groupIds
	 *
	 * @param groupIds
	 * @param isDevicePod
	 * @return
	 * @throws ItemNotFoundRepositoryException 
	 */
	public List<Device> getAllDevicesByGroupIds(List<String> groupIds) throws ItemNotFoundRepositoryException {
		List<Device> devices = new ArrayList<>();
		List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupIds(groupIds);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				if (!Strings.isNullOrEmpty(license.getDeviceId())) {
					DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());

					if (devInfo != null) {
						DeviceStatus status = devInfo.getDeviceStatus();

						DeviceStatus deviceStatus = getDeviceStatus(devInfo);
						if (status != deviceStatus) {
							devInfo.setDeviceStatus(deviceStatus);
							deviceInfoRepository.update(devInfo);
						}

						CompanyGroup group = groupRepository.find(license.getGroupId());
						Device device = new Device(group, devInfo);
						devices.add(device);
					}
				}

			}
		}

		return devices;

	}

	/**
	 * This function returns all pods from the current context with merged Test objects.
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllDevicesFromCurrentContextPagination(Context context, int page, int size) throws ItemNotFoundRepositoryException {
		log.debug("Retrieving devices for the context {}", context.getName());
		List<Device> devices = new ArrayList<>();
		Map<String, Object> deviceMap = new HashMap<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		List<String> groupIds = portalUtils.extractIdsFromObjects(assignedGroups);

		Map<String, Object> licenseMap = licenseRepository.findByGroupIdsPaged(groupIds, page, size);

		if (licenseMap != null) {
			List<CompanyLicensePrivate> licenses = (List<CompanyLicensePrivate>) licenseMap.get("licenses");
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (!Strings.isNullOrEmpty(license.getDeviceId())) {
						DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());
						if (devInfo != null) {
							DeviceStatus status = devInfo.getDeviceStatus();

							DeviceStatus deviceStatus = getDeviceStatus(devInfo);
							if (status != deviceStatus) {
								devInfo.setDeviceStatus(deviceStatus);
								deviceInfoRepository.update(devInfo);
							}
							CompanyGroup group = groupRepository.find(license.getGroupId());
							Device device = new Device(group, devInfo);
							devices.add(device);
						}
					}
				}
			}

			deviceMap.put("devices", devices);
			deviceMap.put("totalSize", licenseMap.get("totalSize"));
		}
		log.debug("Retrieved {} devices for context {}", devices.size(), context.getName());
		return deviceMap;
	}
	
	/**
	 * This function returns all pods from the current context with merged Test objects.
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllDevicesByIdAndTypeFromCurrentContextPagination(Context context, String type, int page, int size) throws ItemNotFoundRepositoryException {
		log.debug("Retrieving devices for the context {}", context.getName());
		List<Device> devices = new ArrayList<>();
		Map<String, Object> deviceMap = new HashMap<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		List<String> groupIds = portalUtils.extractIdsFromObjects(assignedGroups);

		Map<String, Object> licenseMap = licenseRepository.findByGroupIdsPaged(groupIds, page, size);

		if (licenseMap != null) {
			List<CompanyLicensePrivate> licenses = (List<CompanyLicensePrivate>) licenseMap.get("licenses");
			List<DeviceInfo> devInfoList = deviceInfoRepository.findByDeviceType(type);
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (!Strings.isNullOrEmpty(license.getDeviceId())) {
						for(DeviceInfo devInfo : devInfoList) {
							if(devInfo.getDeviceId().equals(license.getDeviceId())) {
								if (devInfo != null) {
									DeviceStatus status = devInfo.getDeviceStatus();

									DeviceStatus deviceStatus = getDeviceStatus(devInfo);
									if (status != deviceStatus) {
										devInfo.setDeviceStatus(deviceStatus);
										deviceInfoRepository.update(devInfo);
									}
									CompanyGroup group = groupRepository.find(license.getGroupId());
									Device device = new Device(group, devInfo);
									devices.add(device);
								}
							}
						}
					}
				}
			}

			deviceMap.put("devices", devices);
			deviceMap.put("totalSize", licenseMap.get("totalSize"));
		}
		log.debug("Retrieved {} devices for context {}", devices.size(), context.getName());
		return deviceMap;
	}

	/**
	 * This function deletes the device dependencies for the specified device id
	 *
	 * @param deviceId
	 */
	public void deleteDependencies(String deviceId) {
		if (!Strings.isNullOrEmpty(deviceId)) {
			// TODO - check before deleting if we need to decrement the number of downloaded licenses in context
			licenseRepository.deleteByDeviceId(deviceId);
			log.debug("Deleted dependencies for probe id {}", deviceId);
		}

	}

	/**
	 * This method checks the current status (online, offline, unknown) of the pod according to the lastOnlineTimestamp
	 *
	 * @param deviceInfo
	 * @return
	 */
	private DeviceStatus getDeviceStatus(DeviceInfo devInfo) {
		// make it multilingual
		if (devInfo.getLastOnlineTimestamp() == 0) {
			return DeviceStatus.UNKNOWN;
		} else {
			long timeDiff = System.currentTimeMillis() - devInfo.getLastOnlineTimestamp();
			if (timeDiff > 60000) {
				return DeviceStatus.OFFLINE;
			} else {
				return DeviceStatus.ONLINE;
			}
		}
	}

}
