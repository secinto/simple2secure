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
import java.util.List;
import java.util.Map;

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
import com.simple2secure.api.dto.TestSUTDataInput;
import com.simple2secure.api.dto.TestStatusDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.FactToCheckByRuleEngine;
import com.simple2secure.api.model.ReportType;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputTest;
import com.simple2secure.portal.validation.model.ValidInputTestRun;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.TEST_API)
@Slf4j
public class TestController extends BaseUtilsProvider {

	/*
	 * ----------------------------------------------------------------------------- --------------------------------------------------------
	 *
	 * WEB Interfaces
	 *
	 * ----------------------------------------------------------------------------- --------------------------------------------------------
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
	public ResponseEntity<Map<String, Object>> getTestByDeviceId(@PathVariable ValidInputDevice deviceId, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@RequestParam boolean usePagination, @ServerProvidedValue ValidInputLocale locale) {

		return testUtils.getTestByDeviceId(deviceId.getValue(), page, size, usePagination, locale, filter);
	}

	@ValidRequestMapping(
			value = "/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<Test> deleteTest(@PathVariable ValidInputTest testId, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (testId.getValue() != null) {
			Test test = testRepository.find(testId.getValue());
			if (testUtils.hasUserRightsForTest(test, user.getValue())) {
				if (test != null) {
					testUtils.deleteTest(test);
					return new ResponseEntity<>(test, HttpStatus.OK);
				}
			} else {
				throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_has_no_rights", locale.getValue()));
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/clone",
			method = ValidRequestMethodType.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<Test> cloneTest(@PathVariable ValidInputTest testId, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (testId.getValue() != null) {
			Test test = testRepository.find(testId.getValue());
			if (test != null) {
				Test clonedTest = testUtils.cloneTest(test);

				if (clonedTest != null) {
					return new ResponseEntity<>(test, HttpStatus.OK);
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_cloning_test", locale.getValue()));
	}

	/**
	 * This function adds a test to the schedule if it is started via the portal. This can be done by any user. The selected test is then
	 * added to the internal schedule which is continuously checked by the pods. During the preparation also SUTs are added to the test
	 * content, if those are provided.
	 *
	 * @param testSutDatainput
	 *          The test object to add to the schedule
	 * @param contextId
	 *          The context for which this has been performed
	 * @param userId
	 *          The user which added it to the schedule
	 * @param locale
	 *          The current locale used by the user
	 * @return A test run which has been created.
	 */
	@ValidRequestMapping(
			value = "/scheduleTest",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> addTestToSchedule(@RequestBody TestSUTDataInput testSutDatainput,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (testSutDatainput != null && contextId.getValue() != null && !Strings.isNullOrEmpty(userId.getValue())) {

			Test currentTest = testRepository.find(testSutDatainput.getTest().getTestId());

			if (currentTest != null) {

				String testContent = testUtils.mergeSUTAndDataInput(testSutDatainput, currentTest);

				TestRun testRun = new TestRun(currentTest.getId(), currentTest.getName(), currentTest.getPodId(), contextId.getValue(),
						TestRunType.MANUAL_PORTAL, testContent, TestStatus.PLANNED, System.currentTimeMillis());
				testRun.setHostname(testSutDatainput.getTest().getHostname());
				testRunRepository.save(testRun);

				/*
				 * notificationUtils.addNewNotificationPortal(test.getName() + " has been scheduled using the portal by " + user.getEmail(),
				 * contextId.getValue());
				 */
				return new ResponseEntity<>(testRun, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/getScheduledTests")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getScheduledTestsByContextId(@ServerProvidedValue ValidInputContext contextId, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null) {

			Map<String, Object> tests = testRunRepository.getByContextIdForPagination(contextId.getValue(), page, size, filter);

			if (tests.isEmpty()) {
				throw new ApiRequestException(messageByLocaleService.getMessage("scheduled_tests_not_provided", locale.getValue()));
			}

			return new ResponseEntity<>(tests, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/testrun/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> deleteTestRun(@PathVariable ValidInputTestRun testRunId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (testRunId.getValue() != null) {
			TestRun testRun = testRunRepository.find(testRunId.getValue());
			if (testRun != null) {
				testRunRepository.delete(testRun);
				return new ResponseEntity<>(testRun, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/save",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<Test> updateSaveTest(@RequestBody TestObjWeb test, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException, NoSuchAlgorithmException {
		if (test != null) {
			Test convertedTest = testUtils.convertTestWebObjtoTestObject(test);
			if (testUtils.hasUserRightsForTest(convertedTest, user.getValue())) {
				if (convertedTest != null) {

					testRepository.save(convertedTest);
					return new ResponseEntity<>(convertedTest, HttpStatus.OK);
				} else {
					throw new ApiRequestException(
							messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale.getValue()));
				}
			} else {
				throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_has_no_rights", locale.getValue()));
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue()));
	}

	/*
	 * ----------------------------------------------------------------------------- --------------------------------------------------------
	 *
	 * POD Interfaces
	 *
	 * ----------------------------------------------------------------------------- --------------------------------------------------------
	 */

	/**
	 * Returns a list of tests available from the specified pod. This function is used from the web.
	 *
	 * @param deviceId
	 *          The ID to identify the POD
	 * @param locale
	 * @return
	 */
	@ValidRequestMapping(
			value = "/byTestId")
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<Test> getTestById(@PathVariable ValidInputTest testId, @ServerProvidedValue ValidInputLocale locale) {

		if (testId.getValue() != null) {
			Test test = testRepository.find(testId.getValue());
			if (test != null) {
				return new ResponseEntity<>(test, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/scheduleTestPod",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<TestRun> addTestToSchedulePod(@RequestBody Test test, @PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (test != null && deviceId.getValue() != null) {
			CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId.getValue());
			DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());

			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());

				if (group != null) {
					String test_content = test.getTestContent().replace("\'", "\"");

					TestRun testRun = new TestRun(test.getId(), test.getName(), deviceId.getValue(), group.getContextId(), TestRunType.MANUAL_POD,
							test_content, TestStatus.PLANNED, System.currentTimeMillis());
					testRun.setHostname(deviceInfo.getName());

					testRunRepository.save(testRun);

					notificationUtils.addNewNotification(
							test.getName() + " has been scheduled for the execution manually using the pod " + deviceInfo.getName(), group.getContextId(),
							null, false);

					return new ResponseEntity<>(testRun, HttpStatus.OK);

				}

			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_scheduling_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/saveTestResult",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @ServerProvidedValue ValidInputLocale locale) {
		TestResult result = testUtils.saveTestResult(testResult, locale.getValue());
		factsToCheckRepository.save(new FactToCheckByRuleEngine(result.getId(), RuleFactType.TESTRESULT, false));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ValidRequestMapping(
			value = "/result/groups",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getTestReportsByGroupIdsAndPagination(@RequestBody List<CompanyGroup> groups, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (groups != null) {

			List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
			if (groupIds != null && !groupIds.isEmpty()) {
				List<Device> devices = deviceUtils.getAllDevicesWithReportsByGroupId(groupIds, DeviceType.POD, ReportType.TEST);
				if (devices != null) {
					List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);
					if (deviceIds != null && !deviceIds.isEmpty()) {
						Map<String, Object> testResults = testResultRepository.getTestResultsByDeviceIdWithPagination(deviceIds, page, size, filter);
						return new ResponseEntity<>(testResults, HttpStatus.OK);
					} else {
						throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports_device", locale.getValue()));
					}
				}
			}
			throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports_group", locale.getValue()));
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/result/devices",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getTestResultsByDeviceIdsAndPagination(@RequestBody List<Device> devices, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {
		if (devices != null) {

			List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);

			if (deviceIds != null && !deviceIds.isEmpty()) {
				Map<String, Object> testResults = testResultRepository.getTestResultsByDeviceIdWithPagination(deviceIds, page, size, filter);
				return new ResponseEntity<>(testResults, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving test results for groups");
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/result/delete/selected",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestResult>> deleteSelectedTestResults(@RequestBody List<TestResult> testResults,
			@ServerProvidedValue ValidInputLocale locale) {
		if (testResults != null) {
			for (TestResult testResult : testResults) {
				TestResult dbTestResult = testResultRepository.find(testResult.getId());
				if (dbTestResult != null) {
					testResultRepository.delete(dbTestResult);
				}
			}
			return new ResponseEntity<>(testResults, HttpStatus.OK);
		}
		log.error("Error occured while deleting selected test results!");
		throw new ApiRequestException(messageByLocaleService.getMessage("no_reports_provided", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/syncTests",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<List<Test>> syncTestsFromPod(@RequestBody List<Test> tests, @PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (deviceId.getValue() != null) {

			List<Test> device_tests = testRepository.getByDeviceId(deviceId.getValue());

			// Sync those tests only if there are no tests associated with this device id
			if (device_tests == null || device_tests.size() == 0) {
				if (tests != null) {
					for (Test test : tests) {
						if (test != null) {
							test.setPodId(deviceId.getValue());
							test.setLastChangedTimestamp(System.currentTimeMillis());
							test.setActive(true);
							testRepository.save(test);
						}
					}
				}
			}
			device_tests = testRepository.getByDeviceId(deviceId.getValue());

			return new ResponseEntity<>(device_tests, HttpStatus.OK);

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/updateTestStatus",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<TestStatusDTO> updateTestStatus(@RequestBody TestStatusDTO testRunDTO, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (testRunDTO != null) {

			if (testRunDTO.getTestRunId() != null) {
				TestRun testRun = testRunRepository.find(testRunDTO.getTestRunId());

				testRun.setTestStatus(testRunDTO.getTestStatus());

				testRunRepository.update(testRun);
			}

			return new ResponseEntity<>(testRunDTO, HttpStatus.OK);

		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale.getValue()));
	}

}
