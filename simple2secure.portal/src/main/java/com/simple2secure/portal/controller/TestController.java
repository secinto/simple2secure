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
import com.simple2secure.api.model.validation.ValidInputContext;
import com.simple2secure.api.model.validation.ValidInputLocale;
import com.simple2secure.api.model.validation.ValidInputUser;
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
import com.simple2secure.portal.validator.ValidInput;

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
	@RequestMapping(value = "/{deviceId}/{page}/{size}/{usePagination}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Map<String, Object>> getTestByDeviceId(@PathVariable String deviceId, @PathVariable int page, @PathVariable int size,
			@PathVariable boolean usePagination, @ValidInput ValidInputLocale locale) {
		return testUtils.getTestByDeviceId(deviceId, page, size, usePagination, locale.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/{testId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Test> deleteTest(@PathVariable String testId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testId)) {
			Test test = testRepository.find(testId);
			if (test != null) {
				test.setDeleted(true);
				testRepository.update(test);
				return new ResponseEntity<>(test, HttpStatus.OK);
			}
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/scheduleTest/{contextId}/{userId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> addTestToSchedule(@RequestBody TestObjWeb test, @ValidInput ValidInputContext contextId,
			@ValidInput ValidInputUser userId, @ValidInput ValidInputLocale locale) {
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

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/getScheduledTests/{contextId}/{page}/{size}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getScheduledTestsByContextId(@ValidInput ValidInputContext contextId, @PathVariable int page,
			@PathVariable int size, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(locale.getValue())) {

			Map<String, Object> tests = testRunRepository.getByContextIdForPagination(contextId.getValue(), page, size);

			return new ResponseEntity<>(tests, HttpStatus.OK);
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/testresult/{contextId}/{page}/{size}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Map<String, Object>> getTestResultByContextId(@ValidInput ValidInputContext contextId, @PathVariable int page,
			@PathVariable int size, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Map<String, Object> results = testUtils.getTestResultByContextId(contextId.getValue(), locale.getValue(), page, size);
			if (results != null) {
				return new ResponseEntity<>(results, HttpStatus.OK);
			}
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_tests", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/testresult/delete/{testResultId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestResult> deleteTestResult(@PathVariable String testResultId, @ValidInput ValidInputLocale locale) {
		return testUtils.deleteTestResult(testResultId, locale.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/testrun/delete/{testRunId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestRun> deleteTestRun(@PathVariable String testRunId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testRunId)) {
			TestRun testRun = testRunRepository.find(testRunId);
			if (testRun != null) {
				testRunRepository.delete(testRun);
				return new ResponseEntity<>(testRun, HttpStatus.OK);
			}
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Test> updateSaveTest(@RequestBody TestObjWeb test, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException, NoSuchAlgorithmException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && test != null) {

			Test convertedTest = testUtils.convertTestWebObjtoTestObject(test);

			if (convertedTest != null) {

				testRepository.save(convertedTest);
				return new ResponseEntity<>(convertedTest, HttpStatus.OK);
			} else {
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale.getValue())),
						HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity(
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/scheduleTestPod/{deviceId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestRun> addTestToSchedulePod(@RequestBody Test test, @PathVariable String deviceId,
			@ValidInput ValidInputLocale locale) {

		if (test != null && !Strings.isNullOrEmpty(deviceId)) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId);

			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());

				if (group != null) {
					String test_content = test.getTest_content().replace("\'", "\"");
					TestRun testRun = new TestRun(test.getId(), test.getName(), deviceId, group.getContextId(), TestRunType.MANUAL_POD, test_content,
							TestStatus.PLANNED, System.currentTimeMillis());
					testRun.setHostname(license.getDeviceInfo().getHostname());
					testRunRepository.save(testRun);

					notificationUtils.addNewNotificationPortal(
							test.getName() + " has been scheduled for the execution manually using the pod " + license.getDeviceInfo().getHostname(),
							group.getContextId());

					return new ResponseEntity<>(testRun, HttpStatus.OK);

				}

			}
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_scheduling_test", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@RequestMapping(value = "/saveTestResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @ValidInput ValidInputLocale locale) {
		TestResult result = testUtils.saveTestResult(testResult, locale.getValue());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/syncTest", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<Test> syncTestWithPod(@RequestBody Test test, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale.getValue()) && test != null) {
			Test synchronizedTest = testUtils.synchronizeReceivedTest(test);
			return new ResponseEntity<>(synchronizedTest, HttpStatus.OK);
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/syncTests/{deviceId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<Test>> syncTestsWithPod(@RequestBody List<Test> tests, @PathVariable String deviceId,
			@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		List<Test> syncronizedTestList = new ArrayList<>();
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(deviceId)) {
			testUtils.setAllPodTestToUnsyncronized(deviceId);
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

			List<Test> newPortalTests = testUtils.getNewPortalTests(deviceId);
			if (newPortalTests != null && newPortalTests.size() > 0) {
				syncronizedTestList.addAll(newPortalTests);
			}

			testUtils.deleteTaggedPortalTests(deviceId);
			testUtils.deleteUnsyncedTests(deviceId);

			return new ResponseEntity<>(syncronizedTestList, HttpStatus.OK);

		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/updateTestStatus", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestStatusDTO> updateTestStatus(@RequestBody TestStatusDTO testRunDTO, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale.getValue()) && testRunDTO != null) {

			if (!Strings.isNullOrEmpty(testRunDTO.getTestRunId())) {
				TestRun testRun = testRunRepository.find(testRunDTO.getTestRunId());

				testRun.setTestStatus(testRunDTO.getTestStatus());

				testRunRepository.update(testRun);
			}

			return new ResponseEntity<>(testRunDTO, HttpStatus.OK);

		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}
