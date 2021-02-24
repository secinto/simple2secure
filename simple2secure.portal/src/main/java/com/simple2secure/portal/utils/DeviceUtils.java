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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.ReportType;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeviceUtils extends BaseServiceProvider {

	@Autowired
	TestUtils testUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	UserUtils userUtils;

	/**
	 * This function returns all devices from the current context
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
					if (license.getDeviceId() != null) {
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
	 * This functions returns all devices (probes) according to provided groupIds only if they already have some reports in the database.
	 *
	 * @param groupIds
	 * @return
	 */
	public List<Device> getAllDevicesWithReportsByGroupId(List<ObjectId> groupIds, DeviceType deviceType, ReportType reportType) {

		List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupIds(groupIds);
		List<Device> devices = new ArrayList<>();

		boolean hasReports = false;

		if (licenses != null && !licenses.isEmpty()) {

			for (CompanyLicensePrivate license : licenses) {

				if (reportType == ReportType.OSQUERY) {
					List<OsQueryReport> reports = reportsRepository.getReportsByDeviceId(license.getDeviceId());
					if (reports != null && !reports.isEmpty()) {
						hasReports = true;
					}
				} else if (reportType == ReportType.NETWORK) {
					List<NetworkReport> reports = networkReportRepository.getReportsByDeviceId(license.getDeviceId());
					if (reports != null && !reports.isEmpty()) {
						hasReports = true;
					}
				} else if (reportType == ReportType.TEST) {
					List<TestResult> reports = testResultRepository.getByDeviceId(license.getDeviceId());
					if (reports != null && !reports.isEmpty()) {
						hasReports = true;
					}
				} else if (reportType == ReportType.TESTSEQUENCE) {
					List<TestSequenceResult> reports = testSequenceResultRepository.getByDeviceId(license.getDeviceId());
					if (reports != null && !reports.isEmpty()) {
						hasReports = true;
					}
				}

				if (hasReports) {
					DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());

					if (devInfo != null) {
						DeviceStatus status = devInfo.getDeviceStatus();

						DeviceStatus deviceStatus = getDeviceStatus(devInfo);
						if (status != deviceStatus) {
							devInfo.setDeviceStatus(deviceStatus);
							try {
								deviceInfoRepository.update(devInfo);
							} catch (ItemNotFoundRepositoryException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						CompanyGroup group = groupRepository.find(license.getGroupId());
						Device device = new Device(group, devInfo);
						devices.add(device);
					}

				}

				hasReports = false;
			}
		}
		return devices;
	}

	/**
	 * This function returns all devices from the current context with merged Test objects.
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public Map<String, Object> getAllDevicesFromCurrentContextPagination(Context context, int page, int size)
			throws ItemNotFoundRepositoryException {
		log.debug("Retrieving devices for the context {}", context.getName());
		Map<String, Object> deviceMap = new HashMap<>();
		List<Device> deviceList = deviceInfoRepository.findByContextId(context.getId());
		for (Device device : deviceList) {
			updateDeviceStatus(device);
		}
		deviceMap.put("devices", deviceList);
		deviceMap.put("totalSize", deviceList.size());
		return deviceMap;
	}

	/**
	 * This function returns all devices with merged test objects
	 *
	 * @param context
	 * @param type
	 * @param userId
	 * @param page
	 * @param size
	 * @return
	 */
	public Map<String, Object> getAllDevicesByIdAndTypeWithMergedTestObjects(Context context, String type, String userId, int page,
			int size, String filter) {
		log.debug("Retrieving devices for the context {}", context.getName());
		Map<String, Object> deviceMap = new HashMap<>();
		List<Device> deviceList = deviceInfoRepository.findByContextIdAndType(context.getId(), type, page, size, filter);
		List<Device> resultDeviceList = new ArrayList<>();
		for (Device device : deviceList) {
			ContextUserAuthentication cUA = contextUserAuthRepository.getByContextIdAndUserId(device.getGroup().getContextId(), userId);
			if (userUtils.checkIsUserAdminInContext(cUA)) {
				resultDeviceList.add(device);
			}
			try {
				updateDeviceStatus(device);
			} catch (ItemNotFoundRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		deviceMap.put("devices", resultDeviceList);
		deviceMap.put("totalSize", resultDeviceList.size());
		return deviceMap;
	}

	/**
	 * This method updates the publicly available flag for the given Pod.
	 *
	 * @param Device
	 *          ...the device where the flag is going to be updated
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public DeviceInfo updatePodVisibility(Device device) throws ItemNotFoundRepositoryException {
		log.debug("Updating device {}!", device.getInfo().getName());
		deviceInfoRepository.update(device.getInfo());
		updateDeviceStatus(device);

		return device.getInfo();
	}

	public Map<String, Object> getAllDevicesByGroupId(ObjectId groupId, int page, int size, String filter) {

		Map<String, Object> deviceMap = licenseRepository.getDevicesByGroupIdPagination(groupId, page, size, filter);

		return deviceMap;
	}

	/**
	 * This function returns all pods from the current context with merged Test objects.
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public Map<String, Object> getAllPublicPodDevicesPagination(int page, int size, String filter) throws ItemNotFoundRepositoryException {
		log.debug("Retrieving all public pod devices.");

		Map<String, Object> deviceMap = deviceInfoRepository.findAllPublicPodDevices(page, size, filter);
		List<Device> deviceList = (List<Device>) deviceMap.get("devices");
		for (Device device : deviceList) {
			updateDeviceStatus(device);
		}

		
		return deviceMap;
	}

	/**
	 * This function retrieves all devices from the current context according to the deviceType and active state.
	 *
	 * @param contextId
	 * @param active
	 * @param deviceType
	 * @param size
	 * @param page
	 * @return
	 */
	public Map<String, Object> getAllDevicesFromContextWithPaginationByType(ObjectId contextId, boolean active, String deviceType, int size,
			int page, String locale, String filter) {

		// TODO: we have to distinguish what is active device!!!
		Context context = contextRepository.find(contextId);
		Map<String, Object> deviceMap = new HashMap<>();
		if (context != null) {
			if (deviceType.equals(DeviceType.UNKNOWN.toString())) {
				try {
					return getAllDevicesFromCurrentContextPagination(context, page, size);
				} catch (ItemNotFoundRepositoryException e) {
					log.error(e.getLocalizedMessage());
					throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_user_devices", locale));
				}
			} else if (deviceType.equals(DeviceType.POD.toString())) {
				// TODO: must work with pagination
				List<Device> devices = deviceInfoRepository.findByContextIdAndType(contextId, deviceType, page, size, filter);
				deviceMap.put("devices", devices);
				deviceMap.put("totalSize", devices.size());
				if (deviceMap != null && !deviceMap.isEmpty()) {
					return deviceMap;
				}

			} else if (deviceType.equals(DeviceType.PROBE.toString())) {

				if (active) {
					List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
					if (groups != null) {
						List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
						List<Device> devices = getAllDevicesWithReportsByGroupId(groupIds, DeviceType.PROBE, ReportType.OSQUERY);

						deviceMap.put("devices", devices);
						deviceMap.put("totalSize", devices.size());
						if (deviceMap != null && !deviceMap.isEmpty()) {
							return deviceMap;
						}
					}

				} else {
					List<Device> devices = deviceInfoRepository.findByContextIdAndType(contextId, deviceType, page, size, filter);
					deviceMap.put("devices", devices);
					deviceMap.put("totalSize", devices.size());
					if (deviceMap != null && !deviceMap.isEmpty()) {
						return deviceMap;
					}
				}

			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_getting_user_devices", locale));
	}

	/**
	 * This method checks the current status (online, offline, unknown) of the pod according to the lastOnlineTimestamp
	 *
	 * @param deviceInfo
	 * @return
	 *
	 *         TODO: make it multilingual
	 */
	public DeviceStatus getDeviceStatus(DeviceInfo devInfo) {
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

	/**
	 * This function creates a list of devices from the provided licenses
	 *
	 * @param licenses
	 * @return
	 */
	public List<Device> createDeviceObjectsFromLicenses(List<CompanyLicensePrivate> licenses) {
		List<Device> devices = new ArrayList<>();
		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				CompanyGroup group = groupRepository.find(license.getGroupId());
				DeviceInfo devInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());

				if (group != null && devInfo != null) {
					devices.add(new Device(group, devInfo));
				}
			}
		}
		return devices;
	}

	/**
	 * This function updates the device status to the ONLINE, OFFLINE or UNKNOWN
	 *
	 * @param device
	 * @throws ItemNotFoundRepositoryException
	 */
	private void updateDeviceStatus(Device device) throws ItemNotFoundRepositoryException {
		DeviceStatus status = device.getInfo().getDeviceStatus();
		DeviceStatus currentStatus = getDeviceStatus(device.getInfo());
		if (status != currentStatus) {
			device.getInfo().setDeviceStatus(currentStatus);
			deviceInfoRepository.update(device.getInfo());
		}
	}
}
