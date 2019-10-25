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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.DeviceDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class DeviceUtils {

	private static Logger log = LoggerFactory.getLogger(DeviceUtils.class);

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	TestSequenceRepository testSequenceRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	TestUtils testUtils;

	/**
	 * This function returns all probes from the current context
	 *
	 * @param context
	 * @return
	 */
	public List<Device> getAllDevicesFromCurrentContext(Context context) {
		log.debug("Retrieving devices for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<Device> myDevices = new ArrayList<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findAllByGroupId(group.getId());
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.isActivated()) {
						if (!Strings.isNullOrEmpty(license.getDeviceId())) {
							String deviceStatus = getDeviceStatus(license);

							Device probe = new Device(license.getDeviceId(), group, license.isActivated(), license.getHostname(), deviceStatus,
									license.isDevicePod());
							myDevices.add(probe);
						}
					}
				}
			}
		}
		log.debug("Retrieved {0} probes for context {1}", myDevices.size(), context.getName());
		return myDevices;
	}

	/**
	 * This function returns all pods from the current context with merged Test objects.
	 *
	 * @param context
	 * @return
	 */
	public List<DeviceDTO> getAllDevicesFromCurrentContextWithTests(Context context) {
		log.debug("Retrieving pods for the context {}", context.getName());
		/* Set user probes from the licenses - not from the users anymore */
		List<DeviceDTO> myDevices = new ArrayList<>();
		List<CompanyGroup> assignedGroups = groupRepository.findByContextId(context.getId());
		for (CompanyGroup group : assignedGroups) {
			List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupIdAndDeviceType(group.getId(), true);
			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.isActivated()) {
						if (!Strings.isNullOrEmpty(license.getDeviceId())) {
							String deviceStatus = getDeviceStatus(license);
							Device device = new Device(license.getDeviceId(), group, license.isActivated(), license.getHostname(), deviceStatus, true);
							List<TestObjWeb> tests = testUtils.convertToTestObjectForWeb(testRepository.getByPodId(device.getDeviceId()));
							List<TestSequence> test_sequences = testSequenceRepository.getByPodId(device.getDeviceId());
							DeviceDTO deviceDTO = new DeviceDTO(device, tests, test_sequences);
							myDevices.add(deviceDTO);
						}
					}
				}
			}
		}
		log.debug("Retrieved {0} devices for context {1}", myDevices.size(), context.getName());
		return myDevices;
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
	 * @param deviceLicense
	 * @return
	 */
	private String getDeviceStatus(CompanyLicensePrivate deviceLicense) {
		// make it multilingual
		if (deviceLicense.getLastOnlineTimestamp() == 0) {
			return "Unknown";
		} else {
			long timeDiff = System.currentTimeMillis() - deviceLicense.getLastOnlineTimestamp();
			if (timeDiff > 60000) {
				return "Offline";
			} else {
				return "Online";
			}
		}
	}

}