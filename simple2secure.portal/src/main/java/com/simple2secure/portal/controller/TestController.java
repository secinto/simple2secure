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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestResultDTO;
import com.simple2secure.api.dto.TestRunDTO;
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
import com.simple2secure.portal.utils.TestUtils;

@RestController
@RequestMapping("/api/test")
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/scheduleTest/{contextId}/{userId}",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> addTestToSchedule(@RequestBody TestObjWeb test, @PathVariable("contextId") String contextId,
			@PathVariable("userId") String userId, @RequestHeader("Accept-Language") String locale) {
		if (test != null && !Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(userId)) {

			User user = userRepository.find(userId);

			if (user != null) {

				Test currentTest = testRepository.find(test.getTestId());

				if (currentTest != null) {

					TestRun testRun = new TestRun(test.getTestId(), test.getName(), test.getPodId(), contextId, TestRunType.MANUAL_PORTAL,
							currentTest.getTest_content(), TestStatus.PLANNED, System.currentTimeMillis());

					testRunRepository.save(testRun);

					notificationUtils.addNewNotificationPortal(test.getName() + " has been scheduled using the portal by " + user.getEmail(),
							contextId);

					return new ResponseEntity<>(testRun, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/scheduleTestPod/{podId}",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<TestRun> addTestToSchedulePod(@RequestBody Test test, @PathVariable("podId") String podId,
			@RequestHeader("Accept-Language") String locale) {

		if (test != null && !Strings.isNullOrEmpty(podId)) {
			CompanyLicensePrivate license = licenseRepository.findByPodId(podId);

			if (license != null) {
				CompanyGroup group = groupRepository.find(license.getGroupId());

				if (group != null) {
					String test_content = test.getTest_content().replace("\'", "\"");
					TestRun testRun = new TestRun(test.getId(), test.getName(), podId, group.getContextId(), TestRunType.MANUAL_POD, test_content,
							TestStatus.PLANNED, System.currentTimeMillis());

					testRunRepository.save(testRun);

					notificationUtils.addNewNotificationPortal(
							test.getName() + " has been scheduled for the execution manually using the pod " + license.getHostname(),
							group.getContextId());

					return new ResponseEntity<>(testRun, HttpStatus.OK);

				}

			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_scheduling_test", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/getScheduledTests/{contextId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestRunDTO>> getScheduledTestsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(locale)) {

			List<TestRun> tests = testRunRepository.getByContextId(contextId);

			if (tests != null) {
				List<TestRunDTO> testRunDTOList = new ArrayList<>();

				for (TestRun test : tests) {
					TestResult testResult = testResultRepository.getByTestRunId(test.getId());

					TestRunDTO testRunDTO = new TestRunDTO(test, testResult);

					testRunDTOList.add(testRunDTO);
				}
				return new ResponseEntity<>(testRunDTOList, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(
			value = "/saveTestResult",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @RequestHeader("Accept-Language") String locale) {
		return testUtils.saveTestResult(testResult, locale);
	}

	@RequestMapping(
			value = "/testresult/{contextId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<TestResultDTO>> getTestResultByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {
		return testUtils.getTestResultByContextId(contextId, locale);
	}

	@RequestMapping(
			value = "/testresult/delete/{testResultId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestResult> deleteTestResult(@PathVariable("testResultId") String testResultId,
			@RequestHeader("Accept-Language") String locale) {
		return testUtils.deleteTestResult(testResultId, locale);
	}

	@RequestMapping(
			value = "/{podId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<TestObjWeb>> getTestByPodId(@PathVariable("podId") String podId,
			@RequestHeader("Accept-Language") String locale) {
		return testUtils.getTestByPodId(podId, locale);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'POD')")
	public ResponseEntity<Test> updateSaveTest(@RequestBody TestObjWeb test, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, NoSuchAlgorithmException {
		if (!Strings.isNullOrEmpty(locale) && test != null) {

			Test convertedTest = testUtils.convertTestWebObjtoTestObject(test);

			if (convertedTest != null) {
				if (!Strings.isNullOrEmpty(convertedTest.getPodId())) {
					if (Strings.isNullOrEmpty(test.getTestId())) {
						boolean isSaveable = testUtils.checkIfTestIsSaveable(convertedTest);

						if (isSaveable) {
							testRepository.save(convertedTest);
						} else {
							return new ResponseEntity(
									new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale)),
									HttpStatus.NOT_FOUND);
						}

					} else {
						boolean isUpdateable = testUtils.checkIfTestIsUpdateable(convertedTest);
						if (isUpdateable) {
							testRepository.update(convertedTest);
						} else {
							return new ResponseEntity(
									new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale)),
									HttpStatus.NOT_FOUND);
						}
					}
					return new ResponseEntity<>(convertedTest, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/saveTestPod",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<Test> updateSaveTestPod(@RequestBody Test test, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale) && test != null) {
			if (!Strings.isNullOrEmpty(test.getPodId())) {
				Test currentPortalTest = testRepository.getTestByNameAndPodId(test.getName(), test.getPodId());
				Test returnTestValue = new Test();

				if (currentPortalTest != null) {
					boolean isPortalTestOlder = testUtils.checkIfPortalTestIsOlder(currentPortalTest.getLastChangedTimestamp(),
							test.getLastChangedTimestamp());

					returnTestValue = currentPortalTest;

					if (isPortalTestOlder) {
						currentPortalTest.setHash_value(test.getHash_value());
						currentPortalTest.setLastChangedTimestamp(test.getLastChangedTimestamp());
						currentPortalTest.setTest_content(test.getTest_content());
						currentPortalTest.setActive(true);
						testRepository.update(currentPortalTest);
					}
				} else {
					currentPortalTest = new Test();
					currentPortalTest.setHash_value(test.getHash_value());
					currentPortalTest.setName(test.getName());
					currentPortalTest.setPodId(test.getPodId());
					currentPortalTest.setHostname(test.getHostname());
					currentPortalTest.setLastChangedTimestamp(test.getLastChangedTimestamp());
					currentPortalTest.setTest_content(test.getTest_content());
					currentPortalTest.setActive(true);

					testRepository.save(currentPortalTest);

					returnTestValue = testRepository.getTestByNameAndPodId(test.getName(), test.getPodId());
				}
				return new ResponseEntity<>(returnTestValue, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/syncTests",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<List<Test>> syncTestsWithPod(@RequestBody List<Test> tests, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale) && tests != null && !tests.isEmpty()) {
			List<Test> portalTests = testRepository.getByPodId(tests.get(0).getPodId());
			List<Test> newTests = new ArrayList<>();
			if (portalTests != null) {
				List<String> podTestNames = testUtils.getTestNamesFromTestList(tests);

				for (Test portalTest : portalTests) {
					if (!podTestNames.contains(portalTest.getName())) {
						newTests.add(portalTest);
					}
				}

				return new ResponseEntity<>(newTests, HttpStatus.OK);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/delete/{testId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Test> deleteTest(@PathVariable("testId") String testId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testId)) {
			Test test = testRepository.find(testId);
			if (test != null) {
				testRepository.delete(test);
				return new ResponseEntity<>(test, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/delete/testrun/{testRunId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestRun> deleteTestRun(@PathVariable("testRunId") String testRunId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(testRunId)) {
			TestRun testRun = testRunRepository.find(testRunId);
			if (testRun != null) {
				testRunRepository.delete(testRun);
				return new ResponseEntity<>(testRun, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/updateTestStatus",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<TestStatusDTO> updateTestStatus(@RequestBody TestStatusDTO testRunDTO,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(locale) && testRunDTO != null) {

			if (!Strings.isNullOrEmpty(testRunDTO.getTestRunId())) {
				TestRun testRun = testRunRepository.find(testRunDTO.getTestRunId());

				testRun.setTestStatus(testRunDTO.getTestStatus());

				testRunRepository.update(testRun);
			}

			return new ResponseEntity<>(testRunDTO, HttpStatus.OK);

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

}
