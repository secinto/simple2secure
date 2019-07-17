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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<TestResult> saveTestResult(TestResult testResult, String locale) {
		if (testResult != null && !Strings.isNullOrEmpty(locale)) {
			if (!Strings.isNullOrEmpty(testResult.getTestId())) {
				testResultRepository.save(testResult);
				return new ResponseEntity<TestResult>(testResult, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_result", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the test results by context id. It collects all groups from the provided context and then iterates over each
	 * group and collects test results from those groups.
	 *
	 * @param contextId
	 * @param locale
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<List<TestResultDTO>> getTestResultByContextId(String contextId, String locale) {
		if (!Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(locale)) {
			List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
			List<TestResultDTO> results = new ArrayList<TestResultDTO>();
			if (groups != null) {
				for (CompanyGroup group : groups) {

					List<CompanyLicensePrivate> licensesByGroup = licenseRepository.findByGroupId(group.getId());

					if (licensesByGroup != null) {
						for (CompanyLicensePrivate license : licensesByGroup) {
							if (!Strings.isNullOrEmpty(license.getPodId())) {
								List<Test> testList = testRepository.getByPodId(license.getPodId());
								if (testList != null) {
									for (Test test : testList) {
										List<TestResult> testResults = testResultRepository.getByTestId(test.getId());

										if (testResults != null) {
											for (TestResult testResult : testResults) {
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
				if (results != null) {
					return new ResponseEntity<List<TestResultDTO>>(results, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test_result", locale)),
				HttpStatus.NOT_FOUND);
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
				return new ResponseEntity<List<TestResultDTO>>(testResults, HttpStatus.OK);
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
				return new ResponseEntity<TestResult>(testResult, HttpStatus.OK);
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
	public ResponseEntity<List<Test>> getTestByPodId(String podId, String locale) {
		if (!Strings.isNullOrEmpty(podId) && !Strings.isNullOrEmpty(locale)) {
			List<Test> testList = testRepository.getByPodId(podId);

			if (testList != null) {
				return new ResponseEntity<List<Test>>(testList, HttpStatus.OK);
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
	public ResponseEntity<List<Test>> getScheduledTestsByPodId(String podId, String locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(podId) && !Strings.isNullOrEmpty(locale)) {

			List<TestRun> testRunList = testRunRepository.getTestNotExecutedByPodId(podId);

			if (testRunList != null) {
				List<Test> testList = new ArrayList();
				for (TestRun testRun : testRunList) {
					if (testRun != null) {
						if (!Strings.isNullOrEmpty(testRun.getTestId())) {
							Test test = testRepository.find(testRun.getTestId());

							if (test != null) {
								testList.add(test);
							}

							testRun.setExecuted(true);
							testRunRepository.update(testRun);
						}
					}
				}
				if (testList != null) {
					return new ResponseEntity<List<Test>>(testList, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

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

}
