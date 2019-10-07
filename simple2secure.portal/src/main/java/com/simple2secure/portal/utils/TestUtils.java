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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.simple2secure.api.dto.TestResultDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class TestUtils {

	private static Logger log = LoggerFactory.getLogger(TestUtils.class);

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TestRunRepository testRunRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	TestUtils testUtils;

	private Gson gson = new Gson();

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
	public List<TestResultDTO> getTestResultByContextId(String contextId, String locale) {
		List<TestResultDTO> results = new ArrayList<>();
		if (!Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(locale)) {
			List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
			if (groups != null) {
				for (CompanyGroup group : groups) {

					List<CompanyLicensePrivate> licensesByGroup = licenseRepository.findByGroupIdAndDeviceType(group.getId(), true);

					if (licensesByGroup != null) {
						for (CompanyLicensePrivate license : licensesByGroup) {
							if (!Strings.isNullOrEmpty(license.getDeviceId())) {

								List<TestRun> testRunList = testRunRepository.getTestRunByPodId(license.getDeviceId());

								if (testRunList != null) {
									for (TestRun testRun : testRunList) {
										TestResult testResult = testResultRepository.getByTestRunId(testRun.getId());

										if (testResult != null) {
											TestResultDTO testResultDTO = new TestResultDTO(testResult, group);
											results.add(testResultDTO);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return results;
	}

	public Test synchronizeReceivedTest(Test test) {
		Test returnTest = new Test();
		Test currentPortalTest = testRepository.getTestByNameAndPodId(test.getName(), test.getPodId());

		if (currentPortalTest != null) {
			boolean isPortalTestOlder = testUtils.checkIfPortalTestIsOlder(currentPortalTest.getLastChangedTimestamp(),
					test.getLastChangedTimestamp());

			returnTest = currentPortalTest;

			if (isPortalTestOlder) {
				currentPortalTest.setHash_value(test.getHash_value());
				currentPortalTest.setLastChangedTimestamp(test.getLastChangedTimestamp());
				currentPortalTest.setTest_content(test.getTest_content());
				currentPortalTest.setActive(true);
				testRepository.save(currentPortalTest);
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

			returnTest = testRepository.getTestByNameAndPodId(test.getName(), test.getPodId());
		}
		return returnTest;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<List<TestResultDTO>> getTestResultsByPodId(String podId, String locale) {
		if (!Strings.isNullOrEmpty(podId) && !Strings.isNullOrEmpty(locale)) {
			List<Test> tests = testRepository.getByPodId(podId);
			List<TestResultDTO> testResults = new ArrayList<>();
			if (tests != null) {
				for (Test test : tests) {
					List<TestResult> testResultByTest = testResultRepository.getByTestId(test.getId());

					if (testResultByTest != null) {
						for (TestResult testResult : testResultByTest) {

							// TODO: check if CompanyGroup is necesarry
							TestResultDTO trDto = new TestResultDTO(testResult, new CompanyGroup("test", null));
							testResults.add(trDto);
						}
					}
				}
			}

			if (testResults != null) {
				return new ResponseEntity<>(testResults, HttpStatus.OK);
			}
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test_result", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function checks if the test result with the provided id exists, and deletes it accordingly.
	 *
	 * @param testResultId
	 * @param locale
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<TestResult> deleteTestResult(String testResultId, String locale) {
		if (!Strings.isNullOrEmpty(testResultId) && !Strings.isNullOrEmpty(locale)) {
			TestResult testResult = testResultRepository.find(testResultId);
			if (testResult != null) {
				testResultRepository.delete(testResult);
				return new ResponseEntity<>(testResult, HttpStatus.OK);
			}
			log.error("Problem occured while deleting test result");
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test_result", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all tests by pod Id.
	 *
	 * @param podId
	 * @param locale
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<List<TestObjWeb>> getTestByPodId(String podId, String locale) {
		if (!Strings.isNullOrEmpty(podId) && !Strings.isNullOrEmpty(locale)) {

			List<TestObjWeb> testsWeb = convertToTestObjectForWeb(testRepository.getByPodId(podId));

			if (testsWeb != null) {
				return new ResponseEntity<>(testsWeb, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all tests which are not executed and which are scheduled for the next run.
	 *
	 * @param hostname
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<List<TestRun>> getScheduledTestsByPodId(String podId, String locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(podId) && !Strings.isNullOrEmpty(locale)) {

			List<TestRun> testRunList = testRunRepository.getPlannedTests(podId);

			if (testRunList != null) {
				return new ResponseEntity<>(testRunList, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale)),
				HttpStatus.NOT_FOUND);
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

		String testContent = gson.toJson(testObjWeb.getTest_content());

		if (Strings.isNullOrEmpty(testObjWeb.getTestId())) {
			// new test
			test.setHostname(testObjWeb.getHostname());
			test.setLastChangedTimestamp(System.currentTimeMillis());
			test.setName(testObjWeb.getName());
			test.setPodId(testObjWeb.getPodId());
			test.setScheduled(testObjWeb.isScheduled());
			test.setScheduledTime(testObjWeb.getScheduledTime());
			test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
			test.setTest_content(testContent);
			test.setHash_value(testUtils.getHexValueHash(testUtils.calculateMd5Hash(testContent)));
			test.setActive(true);

		} else {
			// test should exist in the database, update the existing one
			test = testRepository.find(testObjWeb.getTestId());
			if (test != null) {
				test.setName(testObjWeb.getName());
				test.setScheduled(testObjWeb.isScheduled());
				test.setScheduledTime(testObjWeb.getScheduledTime());
				test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
				test.setTest_content(testContent);
				test.setHash_value(testUtils.getHexValueHash(testUtils.calculateMd5Hash(testContent)));
				test.setLastChangedTimestamp(System.currentTimeMillis());
			} else {
				test = null;
			}
		}

		return test;

	}

	/**
	 * This function calculates the md5 hash of the provided string
	 *
	 * @param content
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] calculateMd5Hash(String content) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(content.getBytes());
		return messageDigest;
	}

	/**
	 * This function converts the byte array with the calculated hash to the hex value represented as string
	 *
	 * @param md5hash
	 * @return
	 */
	public String getHexValueHash(byte[] md5hash) {
		BigInteger no = new BigInteger(1, md5hash);
		String hashtext = no.toString(16);
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}
		return hashtext;
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
				TestContent testContent = gson.fromJson(test.getTest_content(), TestContent.class);
				testObjWeb.setTest_content(testContent);
				testObjWeb.setName(test.getName());
				testObjWeb.setActive(test.isActive());
				testObjWeb.setHostname(test.getHostname());
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

}
