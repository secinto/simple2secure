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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.simple2secure.api.dto.TestStatusDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.TestUtils;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSize;
import simple2secure.validator.model.ValidInputTest;
import simple2secure.validator.model.ValidInputTestResult;
import simple2secure.validator.model.ValidInputTestRun;
import simple2secure.validator.model.ValidInputUser;

@RestController
@RequestMapping(StaticConfigItems.TEST_API)
public class TestController {

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TestRunRepository testRunRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TestUtils testUtils;

	@Autowired
	PortalUtils portalUtils;

	/*
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 *
	 * WEB Interfaces
	 *
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 */

	/**
	 * Returns a list of tests available from the specified pod. This function is used from the web.
	 *
	 * @param deviceId
	 *          The ID to identify the POD
	 * @param locale
	 * @return
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Map<String, Object>> getTestByDeviceId(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputPage page,
			@PathVariable ValidInputSize size, @RequestParam boolean usePagination, @ServerProvidedValue ValidInputLocale locale) {
		return testUtils.getTestByDeviceId(deviceId.getValue(), page.getValue(), size.getValue(), usePagination, locale.getValue());
	}

	@ValidRequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Test> deleteTest(@PathVariable ValidInputTest testId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testId.getValue())) {
			Test test = testRepository.find(testId.getValue());
			if (test != null) {
				test.setDeleted(true);
				testRepository.update(test);
				return new ResponseEntity<>(test, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/scheduleTest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> addTestToSchedule(@RequestBody TestObjWeb test, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputLocale locale) {
		if (test != null && !Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(userId.getValue())) {

			User user = userRepository.find(userId.getValue());

			if (user != null) {

				Test currentTest = testRepository.find(test.getTestId());

				if (currentTest != null) {

					TestRun testRun = new TestRun(test.getTestId(), test.getName(), test.getPodId(), contextId.getValue(), TestRunType.MANUAL_PORTAL,
							currentTest.getTest_content(), TestStatus.PLANNED, System.currentTimeMillis());
					testRun.setHostname(test.getHostname());
					testRunRepository.save(testRun);

					notificationUtils.addNewNotificationPortal(test.getName() + " has been scheduled using the portal by " + user.getEmail(),
							contextId.getValue());

					return new ResponseEntity<>(testRun, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/getScheduledTests")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getScheduledTestsByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(locale.getValue())) {

			Map<String, Object> tests = testRunRepository.getByContextIdForPagination(contextId.getValue(), page.getValue(), size.getValue());

			return new ResponseEntity<>(tests, HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/testresult")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Map<String, Object>> getTestResultByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Map<String, Object> results = testUtils.getTestResultByContextId(contextId.getValue(), locale.getValue(), page.getValue(),
					size.getValue());
			if (results != null) {
				return new ResponseEntity<>(results, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_tests", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/testresult", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestResult> deleteTestResult(@PathVariable ValidInputTestResult testResultId, @ServerProvidedValue ValidInputLocale locale) {
		return testUtils.deleteTestResult(testResultId.getValue(), locale.getValue());
	}

	@ValidRequestMapping(value = "/testrun/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestRun> deleteTestRun(@PathVariable ValidInputTestRun testRunId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testRunId.getValue())) {
			TestRun testRun = testRunRepository.find(testRunId.getValue());
			if (testRun != null) {
				testRunRepository.delete(testRun);
				return new ResponseEntity<>(testRun, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/save", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Test> updateSaveTest(@RequestBody TestObjWeb test, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, NoSuchAlgorithmException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && test != null) {

			Test convertedTest = testUtils.convertTestWebObjtoTestObject(test);

			if (convertedTest != null) {

				testRepository.save(convertedTest);
				return new ResponseEntity<>(convertedTest, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(
						new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/*
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 *
	 * POD Interfaces
	 *
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 */

	@ValidRequestMapping(value = "/scheduleTestPod", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestRun> addTestToSchedulePod(@RequestBody Test test, @PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (test != null && !Strings.isNullOrEmpty(deviceId.getValue())) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());

			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());

				if (group != null) {
					String test_content = test.getTest_content().replace("\'", "\"");
					TestRun testRun = new TestRun(test.getId(), test.getName(), deviceId.getValue(), group.getContextId(), TestRunType.MANUAL_POD,
							test_content, TestStatus.PLANNED, System.currentTimeMillis());
					testRun.setHostname(license.getDeviceInfo().getHostname());
					testRunRepository.save(testRun);

					notificationUtils.addNewNotificationPortal(
							test.getName() + " has been scheduled for the execution manually using the pod " + license.getDeviceInfo().getHostname(),
							group.getContextId());

					return new ResponseEntity<>(testRun, HttpStatus.OK);

				}

			}
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_scheduling_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/saveTestResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @ServerProvidedValue ValidInputLocale locale) {
		TestResult result = testUtils.saveTestResult(testResult, locale.getValue());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ValidRequestMapping(value = "/syncTest", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<Test> syncTestWithPod(@RequestBody Test test, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale.getValue()) && test != null) {
			Test synchronizedTest = testUtils.synchronizeReceivedTest(test);
			return new ResponseEntity<>(synchronizedTest, HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/syncTests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<Test>> syncTestsWithPod(@RequestBody List<Test> tests, @PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		List<Test> syncronizedTestList = new ArrayList<>();
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(deviceId.getValue())) {
			testUtils.setAllPodTestToUnsyncronized(deviceId.getValue());
			if (tests != null) {
				for (Test test : tests) {
					if (test != null) {
						Test synchronizedTest = testUtils.synchronizeReceivedTest(test);
						if (synchronizedTest != null) {
							syncronizedTestList.add(synchronizedTest);
						}
					}
				}
			}

			List<Test> newPortalTests = testUtils.getNewPortalTests(deviceId.getValue());
			if (newPortalTests != null && newPortalTests.size() > 0) {
				syncronizedTestList.addAll(newPortalTests);
			}

			testUtils.deleteTaggedPortalTests(deviceId.getValue());
			testUtils.deleteUnsyncedTests(deviceId.getValue());

			return new ResponseEntity<>(syncronizedTestList, HttpStatus.OK);

		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/updateTestStatus", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestStatusDTO> updateTestStatus(@RequestBody TestStatusDTO testRunDTO, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale.getValue()) && testRunDTO != null) {

			if (!Strings.isNullOrEmpty(testRunDTO.getTestRunId())) {
				TestRun testRun = testRunRepository.find(testRunDTO.getTestRunId());

				testRun.setTestStatus(testRunDTO.getTestStatus());

				testRunRepository.update(testRun);
			}

			return new ResponseEntity<>(testRunDTO, HttpStatus.OK);

		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}
