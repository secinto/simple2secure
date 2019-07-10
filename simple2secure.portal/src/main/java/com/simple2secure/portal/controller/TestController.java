package com.simple2secure.portal.controller;

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
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
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
	TestRunRepository testRunRepository;

	@Autowired
	TestUtils testUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/scheduleTest", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestRun> addTestToSchedule(@RequestBody Test test, @RequestHeader("Accept-Language") String locale) {
		if (test != null) {
			TestRun testRun = new TestRun(test.getId(), test.getPodId(), false, TestRunType.MANUAL_PORTAL);
			// TODO: Add notification that test has been scheduled
			testRunRepository.save(testRun);
			return new ResponseEntity<TestRun>(testRun, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/saveTestResult", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @RequestHeader("Accept-Language") String locale) {
		return testUtils.saveTestResult(testResult, locale);
	}

	@RequestMapping(value = "/testresult/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<TestResultDTO>> getTestResultByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {
		return testUtils.getTestResultByContextId(contextId, locale);
	}

	@RequestMapping(value = "/testresult/delete/{testResultId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestResult> deleteTestResult(@PathVariable("testResultId") String testResultId,
			@RequestHeader("Accept-Language") String locale) {
		return testUtils.deleteTestResult(testResultId, locale);
	}

	@RequestMapping(value = "/{podId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<Test>> getTestByPodId(@PathVariable("podId") String podId, @RequestHeader("Accept-Language") String locale) {
		return testUtils.getTestByPodId(podId, locale);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'POD')")
	public ResponseEntity<TestStatus> updateSaveTest(@RequestBody Test test, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		TestStatus status = new TestStatus();
		if (!Strings.isNullOrEmpty(locale) && test != null) {
			if (!Strings.isNullOrEmpty(test.getPodId())) {
				if (Strings.isNullOrEmpty(test.getId())) {
					Test currentTest = testRepository.getTestByName(test.getName());
					boolean isSaveable = testUtils.checkIfTestIsSaveable(currentTest);

					if (isSaveable) {
						status = new TestStatus("Saved", "Test has been saved successfully");
						testRepository.save(test);
					} else {
						status = new TestStatus("Error", messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale));
					}

				} else {
					boolean isUpdateable = testUtils.checkIfTestIsUpdateable(test);
					if (isUpdateable) {
						status = new TestStatus("Updated", "Test has been updated successfully");
						testRepository.update(test);
					} else {
						status = new TestStatus("Error", messageByLocaleService.getMessage("problem_occured_while_saving_test_name_exists", locale));
					}
				}
				return new ResponseEntity<TestStatus>(status, HttpStatus.OK);
			}
		}
		status = new TestStatus("Error", messageByLocaleService.getMessage("problem_occured_while_saving_test", locale));

		return new ResponseEntity<TestStatus>(status, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/saveTestPod", method = RequestMethod.POST)
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
				return new ResponseEntity<Test>(returnTestValue, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

}
