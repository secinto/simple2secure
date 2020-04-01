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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestRunDTO;
import com.simple2secure.api.dto.TestSequenceRunDTO;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.Parameter;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.api.model.TestDefinition;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestStep;
import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class TestUtils extends BaseServiceProvider {

	@Autowired
	PortalUtils portalUtils;

	/**
	 * This function saves the Test Result which has been executed by the pod. Each test result has own groupId according to the group from
	 * the license which has been used for the activation.
	 */
	public TestResult saveTestResult(TestResult testResult, String locale) {
		if (testResult != null && !Strings.isNullOrEmpty(locale)) {
			if (!Strings.isNullOrEmpty(testResult.getTestRunId())) {
				testResult.setId(null);
				testResultRepository.save(testResult);
			}
		}
		return testResult;
	}

	/**
	 * This function returns the test results by context id. It collects all groups from the provided context and then iterates over each
	 * group and collects test results from those groups.
	 *
	 * @param contextId
	 * @param locale
	 * @return
	 */
	public Map<String, Object> getTestResultByContextId(String contextId, String locale, int page, int size) {
		Map<String, Object> testResultsMap = new HashMap<>();

		testResultsMap.put("tests", new ArrayList<TestRunDTO>());
		testResultsMap.put("totalSize", 0);

		if (!Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(locale)) {

			List<TestRun> testRunList = testRunRepository.getByContextId(contextId);
			if (testRunList != null) {
				List<String> testRunIds = portalUtils.extractIdsFromObjects(testRunList);
				if (testRunIds != null && testRunIds.size() > 0) {
					List<TestResult> testResults = testResultRepository.getByTestRunIdWithPagination(testRunIds, page, size);

					if (testResults != null && testResults.size() > 0) {
						long count = testResultRepository.getTotalAmountOfTestResults(testRunIds);

						List<TestRunDTO> testRunDto = generateTestRunDTOByTestResults(testResults);
						if (testRunDto != null && testRunDto.size() > 0) {
							testResultsMap.put("tests", testRunDto);
							testResultsMap.put("totalSize", count);
						}
					}
				}
			}
		}
		return testResultsMap;
	}

	public Test synchronizeReceivedTest(Test test) {
		Test returnTest = new Test();
		Test currentPortalTest = testRepository.getTestByNameAndDeviceId(test.getName(), test.getPodId());

		if (currentPortalTest != null) {
			boolean isPortalTestOlder = checkIfPortalTestIsOlder(currentPortalTest.getLastChangedTimestamp(), test.getLastChangedTimestamp());

			returnTest = currentPortalTest;

			if (isPortalTestOlder) {
				currentPortalTest.setHash_value(test.getHash_value());
				currentPortalTest.setName(test.getName());
				currentPortalTest.setPodId(test.getPodId());
				currentPortalTest.setLastChangedTimestamp(test.getLastChangedTimestamp());
				currentPortalTest.setTest_content(test.getTest_content());
				currentPortalTest.setActive(true);
				currentPortalTest.setSynced(true);
				testRepository.save(currentPortalTest);
			}
		} else {
			currentPortalTest = new Test();
			currentPortalTest.setHash_value(test.getHash_value());
			currentPortalTest.setName(test.getName());
			currentPortalTest.setPodId(test.getPodId());
			currentPortalTest.setLastChangedTimestamp(test.getLastChangedTimestamp());
			currentPortalTest.setTest_content(test.getTest_content());
			currentPortalTest.setActive(true);
			currentPortalTest.setSynced(true);

			testRepository.save(currentPortalTest);

			returnTest = testRepository.getTestByNameAndDeviceId(test.getName(), test.getPodId());
		}
		return returnTest;
	}

	/**
	 * This function checks if the test result with the provided id exists, and deletes it accordingly.
	 *
	 * @param testResultId
	 * @param locale
	 * @return
	 */

	public ResponseEntity<TestResult> deleteTestResult(String testResultId, ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(testResultId) && !Strings.isNullOrEmpty(locale.getValue())) {
			TestResult testResult = testResultRepository.find(testResultId);
			if (testResult != null) {
				testResultRepository.delete(testResult);
				return new ResponseEntity<>(testResult, HttpStatus.OK);
			}
			log.error("Problem occured while deleting test result");
		}

		return ((ResponseEntity<TestResult>) buildResponseEntity("problem_occured_while_deleting_test_result", locale));

	}

	/**
	 * This function returns all tests by pod Id.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 */
	public ResponseEntity<Map<String, Object>> getTestByDeviceId(String deviceId, int page, int size, boolean usePagination,
			ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(deviceId) && !Strings.isNullOrEmpty(locale.getValue())) {
			Map<String, Object> testMap = new HashMap<>();
			List<TestObjWeb> testsWeb = convertToTestObjectForWeb(
					testRepository.getByDeviceIdWithPagination(deviceId, page, size, usePagination));

			if (testsWeb != null) {
				testMap.put("tests", testsWeb);
				testMap.put("totalSize", testRepository.getCountOfTestsWithDeviceId(deviceId));
				return new ResponseEntity<>(testMap, HttpStatus.OK);
			}
		}
		return ((ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_retrieving_test", locale));
	}

	/**
	 * This function returns all tests by pod Id.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 */
	public ResponseEntity<List<SequenceRun>> getSequenceByDeviceId(String deviceId, ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(deviceId) && !Strings.isNullOrEmpty(locale.getValue())) {

			List<SequenceRun> sequenceRuns = sequenceRunRepository.getSequenceRunByDeviceId(deviceId);

			if (sequenceRuns != null) {
				return new ResponseEntity<>(sequenceRuns, HttpStatus.OK);
			}
		}
		return ((ResponseEntity<List<SequenceRun>>) buildResponseEntity("problem_occured_while_retrieving_test", locale));
	}

	/**
	 * This function returns all tests which are not executed and which are scheduled for the next run.
	 *
	 * @param hostname
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public ResponseEntity<List<TestRun>> getScheduledTestsByDeviceId(String deviceId, ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(deviceId) && !Strings.isNullOrEmpty(locale.getValue())) {

			List<TestRun> testRunList = testRunRepository.getPlannedTests(deviceId);

			if (testRunList != null) {
				return new ResponseEntity<>(testRunList, HttpStatus.OK);
			}
		}

		return ((ResponseEntity<List<TestRun>>) buildResponseEntity("problem_occured_while_retrieving_test", locale));
	}

	/**
	 * This function checks if the test with the provided test name already exists in the database.
	 *
	 * @param test
	 * @return
	 */

	public boolean checkIfTestIsSaveable(Test test) {

		Test dbTest = testRepository.getTestByName(test.getName());

		if (dbTest == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This function checks if test exists in the database according to the test name, if it is true - this test will be updated. If not, then
	 * this test will be saved.
	 *
	 * @param currentTest
	 * @return
	 */
	public boolean checkIfTestIsUpdateable(Test currentTest) {
		Test dbTest = testRepository.getTestByName(currentTest.getName());

		if (dbTest == null) {
			return true;
		} else {
			if (currentTest.getName().equals(dbTest.getName())) {
				return true;
			} else {
				return false;
			}
		}

	}

	/**
	 * This function which test is older, test saved in the mongo db or test retrieved from the pod
	 *
	 * @param portalTestTimestamp
	 * @param podTestTimestamp
	 * @return
	 */
	public boolean checkIfPortalTestIsOlder(long portalTestTimestamp, long podTestTimestamp) {
		if (portalTestTimestamp - podTestTimestamp < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This function converts the provided testObjWeb to the normal Test obj which will be saved or updated in the database.
	 *
	 * @param testObjWeb
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public Test convertTestWebObjtoTestObject(TestObjWeb testObjWeb) throws NoSuchAlgorithmException {

		Test test = new Test();

		testObjWeb.getTest_content().setName(testObjWeb.getName());

		// TODO: change it!
		if (Strings.isNullOrEmpty(testObjWeb.getTest_content().getTest_definition().getVersion())) {
			testObjWeb.getTest_content().getTest_definition().setVersion("0.0.1");
		}

		String testContent = JSONUtils.toString(testObjWeb.getTest_content());

		if (Strings.isNullOrEmpty(testObjWeb.getTestId())) {
			// new test
			test.setLastChangedTimestamp(System.currentTimeMillis());
			test.setName(testObjWeb.getName());
			test.setPodId(testObjWeb.getPodId());
			test.setScheduled(testObjWeb.isScheduled());
			test.setScheduledTime(testObjWeb.getScheduledTime());
			test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
			test.setTest_content(testContent);
			test.setHash_value(CryptoUtils.generateSecureHashHexString(testContent));
			test.setActive(true);
			test.setNewTest(true);

		} else {
			// test should exist in the database, update the existing one
			test = testRepository.find(testObjWeb.getTestId());
			if (test != null) {
				test.setName(testObjWeb.getName());
				test.setPodId(testObjWeb.getPodId());
				test.setScheduled(testObjWeb.isScheduled());
				test.setScheduledTime(testObjWeb.getScheduledTime());
				test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
				test.setTest_content(testContent);
				test.setHash_value(CryptoUtils.generateSecureHashHexString(testContent));
				test.setLastChangedTimestamp(System.currentTimeMillis());
			} else {
				test = null;
			}
		}

		return test;

	}

	/**
	 * This function extracts only test names from the test object list
	 *
	 * @param tests
	 * @return
	 */
	public List<String> getTestNamesFromTestList(List<Test> tests) {
		ArrayList<String> test_names = new ArrayList<>();

		for (Test test : tests) {
			test_names.add(test.getName());
		}

		return test_names;
	}

	/**
	 * This function converts the test object which is saved in the mongo database to the correct test object which is being shown in the web.
	 *
	 * @param tests
	 */
	public List<TestObjWeb> convertToTestObjectForWeb(List<Test> tests) {
		List<TestObjWeb> testsWeb = new ArrayList<>();

		if (tests != null) {

			for (Test test : tests) {
				TestObjWeb testObjWeb = new TestObjWeb();
				TestContent testContent = JSONUtils.fromString(test.getTest_content(), TestContent.class);
				testObjWeb.setTest_content(testContent);
				testObjWeb.setName(test.getName());
				testObjWeb.setActive(test.isActive());
				testObjWeb.setPodId(test.getPodId());
				testObjWeb.setTestId(test.getId());
				testObjWeb.setScheduled(test.isScheduled());
				testObjWeb.setScheduledTime(test.getScheduledTime());
				testObjWeb.setScheduledTimeUnit(test.getScheduledTimeUnit());
				testsWeb.add(testObjWeb);
			}
		}

		return testsWeb;
	}

	/**
	 * This function generates the list of the TestRunDTO for the provided TestResults. For each test result, TestRun object is added.
	 *
	 * @param results
	 * @return
	 */
	public List<TestRunDTO> generateTestRunDTOByTestResults(List<TestResult> results) {
		List<TestRunDTO> testRunDto = new ArrayList<>();
		for (TestResult testResult : results) {
			TestRun testRun = testRunRepository.find(testResult.getTestRunId());
			if (testRun != null) {
				testRunDto.add(new TestRunDTO(testRun, testResult));
			}
		}
		return testRunDto;
	}

	/**
	 * This function generates the list of the TestSequenceRunDTO for the provided sequenceRuns.
	 *
	 * @param sequenceRuns
	 * @return
	 */
	public List<TestSequenceRunDTO> generateSequenceRunDTOBySequenceRun(List<SequenceRun> sequenceRuns) {
		List<TestSequenceRunDTO> sequenceRunDTOs = new ArrayList<>();
		if (sequenceRuns != null) {
			for (SequenceRun sequenceRun : sequenceRuns) {
				if (sequenceRun != null) {
					TestSequenceResult sequenceResult = testSequenceResultRepository.getBySequenceRunId(sequenceRun.getId());
					sequenceRunDTOs.add(new TestSequenceRunDTO(sequenceRun, sequenceResult));
				}
			}
		}
		return sequenceRunDTOs;
	}

	/**
	 * This function adds new portal test to the list which will be returned to the pod. Before returning the flag newTest is set to false and
	 * updated in the database accordingly
	 *
	 * @param deviceId
	 * @return
	 */
	public List<Test> getNewPortalTests(String deviceId) {
		List<Test> newPortalTests = new ArrayList<>();
		newPortalTests = testRepository.getNewPortalTestsByDeviceId(deviceId);

		for (Test test : newPortalTests) {
			test.setNewTest(false);
			test.setSynced(true);
			try {
				testRepository.update(test);
			} catch (ItemNotFoundRepositoryException e) {
				log.error(e.getMessage());
			}
		}

		return newPortalTests;
	}

	/**
	 * This function iterates over all tests which are tagged to be deleted, and deletes them from the database.
	 *
	 * @param syncTests
	 * @param deviceId
	 * @return
	 */
	public void deleteTaggedPortalTests(String deviceId) {
		List<Test> testsToBeDeleted = testRepository.getDeletedTestsByDeviceId(deviceId);
		if (testsToBeDeleted != null) {
			for (Test test : testsToBeDeleted) {
				testRepository.delete(test);
			}
		}
	}

	/**
	 * This function iterates over all tests which are not syncronized, and deletes them from the database.
	 *
	 * @param syncTests
	 * @param deviceId
	 * @return
	 */
	public void deleteUnsyncedTests(String deviceId) {
		List<Test> testsToBeDeleted = testRepository.getUnsyncedTestsByDeviceId(deviceId);
		if (testsToBeDeleted != null) {
			for (Test test : testsToBeDeleted) {
				testRepository.delete(test);
			}
		}
	}

	/**
	 * This tests iterates over all pod tests and sets them to unsyncronized before the syncronization begins.
	 *
	 * @param deviceId
	 */
	public void setAllPodTestToUnsyncronized(String deviceId) {
		List<Test> tests = testRepository.getByDeviceId(deviceId);
		for (Test test : tests) {
			if (test != null) {
				test.setSynced(false);
				try {
					testRepository.update(test);
				} catch (ItemNotFoundRepositoryException e) {
					log.debug(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * This tests iterates over all pod tests and sets them to unsyncronized before the syncronization begins.
	 *
	 * @param deviceId
	 */
	public TestContent getTestContent(Test test) {
		JsonNode testContent = JSONUtils.fromString(test.getTest_content());
		String name = testContent.findValue("name").asText();
		JsonNode testDefinition = testContent.findValue("test_definition");
		JsonNode step = testDefinition.findValue("step");
		String stepDescription = step.findValue("description").asText();
		JsonNode command = step.findValue("command");
		String commandExecutable = command.findValue("executable").asText();
		JsonNode parameter = command.findValue("parameter");
		String parameterDescription = parameter.findValue("description").asText();
		String parameterPrefix = parameter.findValue("prefix").asText();
		String parameterValue = parameter.findValue("value").asText();
		Parameter param = new Parameter();
		param.setDescription(parameterDescription);
		param.setPrefix(parameterPrefix);
		param.setValue(parameterValue);
		Command c = new Command();
		c.setExecutable(commandExecutable);
		c.setParameter(param);
		TestStep tS = new TestStep();
		tS.setCommand(c);
		tS.setDescription(stepDescription);
		TestDefinition tD = new TestDefinition(testDefinition.findValue("description").asText(), testDefinition.findValue("version").asText(), tS, tS, tS);
		return new TestContent(name, tD);
	}
	
	

}
