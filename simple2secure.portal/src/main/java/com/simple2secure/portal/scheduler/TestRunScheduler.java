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
package com.simple2secure.portal.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.commons.time.TimeUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.DeviceInfoRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.TestUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestRunScheduler {

	@Autowired
	TestUtils testUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	TestRepository testRepository;

	@Autowired
	TestRunRepository testRunRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	DeviceInfoRepository deviceInfoRepository;

	@Autowired
	NotificationUtils notificationUtils;

	/**
	 * This function checks if there are some tests which need to be executed and adds those test to the TestRun table in the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 *
	 */

	@Scheduled(fixedRate = 50000)
	public void checkTests() throws ItemNotFoundRepositoryException {

		List<Test> tests = testRepository.getScheduledTest();

		if (tests != null && !tests.isEmpty()) {

			for (Test test : tests) {
				long currentTimestamp = System.currentTimeMillis();
				// Calculate the difference between last execution time and current timestamp

				long millisScheduled = TimeUtils.convertTimeUnitsToMilis(test.getScheduledTime(), test.getScheduledTimeUnit());
				long nextExecutionTime = test.getLastExecution() + millisScheduled;
				long executionTimeDifference = nextExecutionTime - currentTimestamp;

				if (test.getLastExecution() == 0 || executionTimeDifference < 0) {

					CompanyLicensePrivate license = licenseRepository.findByDeviceId(test.getPodId());
					DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());

					if (license != null) {

						CompanyGroup group = groupRepository.find(license.getGroupId());

						if (group != null) {
							TestRun testRun = new TestRun(test.getId(), test.getName(), test.getPodId(), group.getContextId(),
									TestRunType.AUTOMATIC_PORTAL, test.getTest_content(), TestStatus.PLANNED, System.currentTimeMillis());
							testRun.setHostname(deviceInfo.getName());

							test.setLastExecution(currentTimestamp);
							testRunRepository.save(testRun);
							testRepository.update(test);

							notificationUtils.addNewNotificationPortal(test.getName() + " has been scheduled automatically using the portal",
									group.getContextId());
						}
					}
				}
			}
		} else {
			log.info("There are no test cases which need to be scheduled");
		}
	}
}
