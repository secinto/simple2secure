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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestSUTDataInput;
import com.simple2secure.api.dto.TestSequenceRunDTO;
import com.simple2secure.api.dto.TestWebDTO;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.Parameter;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.api.model.TestDefinition;
import com.simple2secure.api.model.TestInputData;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.TestStep;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class TestUtils extends BaseServiceProvider {

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	SUTUtils sutUtils;

	@Autowired
	InputDataUtils inputDataUtils;

	@Autowired
	NotificationUtils notificationUtils;

	/**
	 * This function saves the Test Result which has been executed by the pod. Each test result has own groupId according to the group from
	 * the license which has been used for the activation. 
	 */
	public TestResult saveTestResult(TestResult testResult, String locale) {
		if (testResult != null) {
			if (testResult.getTestRunId() != null) {
				testResult.setId(null);

				DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(testResult.getDeviceId());

				if (deviceInfo != null) {
					testResult.setHostname(deviceInfo.getName());
				}
				TestRun testRun = testRunRepository.find(testResult.getTestRunId());
				testRun.setTestStatus(TestStatus.EXECUTED);
				try {
					testRunRepository.update(testRun);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("A problem occured while updating the status of the test run: ", testRun.getId());
				}
				
				notificationUtils.addNewNotification(
						testRun.getTestName() + " has been executed by the pod " + testResult.getHostname(), testRun.getContextId(),
						null, false);
				
				ObjectId testResultId = testResultRepository.saveAndReturnId(testResult);
				testResult.setId(testResultId);
			}
		}
		return testResult;
	}

	/**
	 * This function returns all tests by pod Id.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 */
	public ResponseEntity<Map<String, Object>> getTestByDeviceId(ObjectId deviceId, int page, int size, boolean usePagination,
			ValidInputLocale locale, String filter) {
		if (deviceId != null) {
			Map<String, Object> testMap = new HashMap<>();
			List<TestObjWeb> testsWeb = convertToTestObjectForWeb(
					testRepository.getByDeviceIdWithPagination(deviceId, page, size, usePagination, filter));
			List<TestWebDTO> testWebList = new ArrayList<>();
			if (testsWeb != null) {
				testWebList = createTestWebDTOsFromTestWebObjList(testsWeb);
				testMap.put("tests", testWebList);
				testMap.put("totalSize", testRepository.getCountOfTestsWithDeviceId(deviceId));
				return new ResponseEntity<>(testMap, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue()));
	}

	/**
	 * This function returns all tests by pod Id.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 */
	public ResponseEntity<List<SequenceRun>> getSequenceByDeviceId(ObjectId deviceId, ValidInputLocale locale) {
		if (deviceId != null) {

			List<SequenceRun> sequenceRuns = sequenceRunRepository.getSequenceRunByDeviceId(deviceId);

			if (sequenceRuns != null) {
				return new ResponseEntity<>(sequenceRuns, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue()));
	}
	
    /**
     * This function returns all tests which are not executed and which are scheduled for the next run.
     *
     * @param hostname
     * @param locale
     * @return
     * @throws ItemNotFoundRepositoryException
     */
    public ResponseEntity<List<TestRun>> getScheduledTestsByDeviceId(ObjectId deviceId, ValidInputLocale locale)
            throws ItemNotFoundRepositoryException {
        if (deviceId != null) {

            List<TestRun> testRunList = testRunRepository.getPlannedTests(deviceId);

            if (testRunList != null) {
                return new ResponseEntity<>(testRunList, HttpStatus.OK);
            }
        }
        throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_test", locale.getValue()));
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

		if (testObjWeb.getTestId() == null) {
			// new test
			test.setLastChangedTimestamp(System.currentTimeMillis());
			test.setName(testObjWeb.getName());
			test.setPodId(testObjWeb.getPodId());
			test.setScheduled(testObjWeb.isScheduled());
			test.setScheduledTime(testObjWeb.getScheduledTime());
			test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
			test.setTestContent(testContent);
			test.setActive(true);

		} else {
			// test should exist in the database, update the existing one
			test = testRepository.find(testObjWeb.getTestId());
			if (test != null) {
				test.setName(testObjWeb.getName());
				test.setPodId(testObjWeb.getPodId());
				test.setScheduled(testObjWeb.isScheduled());
				test.setScheduledTime(testObjWeb.getScheduledTime());
				test.setScheduledTimeUnit(testObjWeb.getScheduledTimeUnit());
				test.setTestContent(testContent);
				test.setLastChangedTimestamp(System.currentTimeMillis());
			} else {
				test = null;
			}
		}

		return test;

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
				TestContent testContent = JSONUtils.fromString(test.getTestContent(), TestContent.class);
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
	 * This function converts the test object which is saved in the mongo database to the correct test object which is being shown in the web.
	 *
	 * @param tests
	 */
	public List<TestObjWeb> convertTestIdsToTestObjectForWeb(List<String> testIds) {
		List<TestObjWeb> testsWeb = new ArrayList<>();

		if (testIds != null) {
			for (String testId : testIds) {
				Test test = testRepository.find(new ObjectId(testId));
				if (test != null) {
					TestObjWeb testObjWeb = new TestObjWeb();
					TestContent testContent = JSONUtils.fromString(test.getTestContent(), TestContent.class);
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
		}

		return testsWeb;
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
	 * This method retrieves the @TestContent object form the given test.
	 *
	 * @param test
	 *          Test from which the @TestContent should be obtained.
	 * @return The @TestContent obtained from the given test.
	 */
	public TestContent getTestContent(String testContentString) {
		JsonNode testContent = JSONUtils.fromString(testContentString);
		String name = testContent.findValue("name").asText();
		TestDefinition testDefinition = getTestDefinition(testContent.findValue("test_definition"));
		return new TestContent(name, testDefinition);
	}

	/**
	 * This method retrieves the @TestDefinition object form the given TestDefinition JsonNode.
	 *
	 * @param testDefinition
	 *          JsonNode from which the @TestDefinition should be obtained.
	 * @return The @TestDefinition obtained from the given JsonNode.
	 */
	public TestDefinition getTestDefinition(JsonNode testDefinition) {
		String description = testDefinition.findValue("description").asText();
		String version = testDefinition.findValue("version").asText();
		TestStep preCondition = getTestStep(testDefinition.findValue("precondition"));
		TestStep step = getTestStep(testDefinition.findValue("step"));
		TestStep postCondition = getTestStep(testDefinition.findValue("postcondition"));
		return new TestDefinition(description, version, preCondition, step, postCondition);
	}

	/**
	 * This method retrieves the @TestStep object form the given TestStep JsonNode.
	 *
	 * @param testStep
	 *          JsonNode from which the @TestStep should be obtained.
	 * @return The @TestStep obtained from the given JsonNode.
	 */
	public TestStep getTestStep(JsonNode testStep) {
		String description = testStep.findValue("description").asText();
		Command command = getCommand(testStep.findValue("command"));
		return new TestStep(description, command);
	}

	/**
	 * This method retrieves the @Command object form the given Command JsonNode.
	 *
	 * @param command
	 *          JsonNode from which the @Command should be obtained.
	 * @return The @Command obtained from the given JsonNode.
	 */
	public Command getCommand(JsonNode command) {
		String executable = command.findValue("executable").asText();
		List<Parameter> parameter = getParameter(command.findValue("parameter"));
		return new Command(executable, parameter);
	}

	/**
	 * This method retrieves the @Parameter object form the given Parameter JsonNode.
	 *
	 * @param parameter
	 *          JsonNode from which the @Parameter should be obtained.
	 * @return The @Parameter obtained from the given JsonNode.
	 */
	public List<Parameter> getParameter(JsonNode parameter) {

		List<Parameter> parameters = new ArrayList<>();

		if (parameter.isArray()) {
			for (JsonNode paramItem : parameter) {
				String description = paramItem.findValue("description").asText();
				String prefix = paramItem.findValue("prefix").asText();
				String value = paramItem.findValue("value").asText();

				parameters.add(new Parameter(description, prefix, value));
			}
		}

		return parameters;
	}

	/**
	 * This method replaces the Value in the Parameter section of the step with the in a SUT defined metadata values.
	 *
	 * @param testContent
	 *          The @TestContent which contains the Parameter value which will be replaced by the SUT metadata.
	 * @param sut
	 *          The @SystemUnderTest from which the metadata will be obtained.
	 * @param sutMetadataKeys
	 *          The list of strings(keys) obtained from the USE_SUT_METADATA flag.
	 * @return A JsonString created from the @TestContent with the replaced values.
	 */
	public String mergeTestAndSutMetadata(TestContent testContent, SystemUnderTest sut, List<String> sutMetadataKeys) {

		// TODO: This must be changed when we change USE_SUT_METADATA
		Map<String, String> metadata = sut.getMetadata();
		if (!sutMetadataKeys.isEmpty() && !metadata.isEmpty()) {
			for (String key : sutMetadataKeys) {
				if (sut.getMetadata().containsKey(key)) {
					for (Parameter param : testContent.getTest_definition().getStep().getCommand().getParameter()) {
						if (param.getValue().equals("USE_SUT_METADATA{" + key + "}")) {
							param.setValue(metadata.get(key));
						}
					}
				}
			}
		}
		return getJsonStringFromTestContent(testContent);
	}

	/**
	 * This method retrieves the JsonString form the given @TestContent.
	 *
	 * @param testContent
	 *          The @TestContent which should be converted into a JsonString.
	 * @return A JsonString obtained from the converted @TestContent.
	 */
	public String getJsonStringFromTestContent(TestContent testContent) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(testContent);
		} catch (JsonProcessingException e) {
			log.error("The Test Content could not be converted to Json String!");
		}
		return jsonString;
	}

	/**
	 * This method checks if the given User has the given rights to edit/delete given test.
	 *
	 * @param ...Test
	 *          The @Test which should be edit/delete-ed.
	 * @return ...true if the User has the rights to edit/delete the Test, false if not.
	 */
	public boolean hasUserRightsForTest(Test test, String userId) {
		CompanyLicensePublic license = licenseRepository.findByDeviceId(test.getPodId());
		CompanyGroup group = groupRepository.find(license.getGroupId());
		ContextUserAuthentication contextUA = contextUserAuthRepository.getByContextIdAndUserId(group.getContextId(), userId);
		if (contextUA != null) {
			if (contextUA.getUserRole().equals(UserRole.SUPERADMIN) || contextUA.getUserRole().equals(UserRole.ADMIN)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function returns the prepared test content for each test.
	 *
	 * @param testSutDatainput
	 * @param currentTest
	 * @return
	 */
	public String mergeSUTAndDataInput(TestSUTDataInput testSutDatainput, Test currentTest) {
		String testContent = currentTest.getTestContent();

		if (testSutDatainput.getSut() != null) {
			testContent = sutUtils.mergeTestContentWithSut(testContent, testSutDatainput.getSut().getId());
		}

		if (testSutDatainput.getInputData() != null) {
			// change input with the value part of the included parameter
			testContent = inputDataUtils.mergeDataInputWithContent(testContent, testSutDatainput.getInputData());
		}

		return testContent;
	}

	/**
	 * This function creates TestWebDTOs from the TestObjWeb List of objects
	 *
	 * @param testWebObjList
	 */
	public List<TestWebDTO> createTestWebDTOsFromTestWebObjList(List<TestObjWeb> testWebObjList) {

		List<TestWebDTO> testWebList = new ArrayList<>();

		for (TestObjWeb testObj : testWebObjList) {
			List<SystemUnderTest> suts = sutUtils.getSutListForTest(testObj);
			List<TestInputData> inputData = testInputDataRepository.getByTestId(testObj.getTestId());
			testWebList.add(new TestWebDTO(testObj, suts, inputData));
		}
		return testWebList;
	}

	/**
	 * This function deletes provided test and all input data accordingly
	 *
	 * @param test
	 */
	public void deleteTest(Test test) {
		try {
			testRepository.delete(test);
			inputDataUtils.deleteInputDataByTestId(test.getId());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}

	/**
	 * This function clones current test and changes only test name
	 *
	 * @param test
	 * @return
	 */
	public Test cloneTest(Test test) {
		Test clonedTest = new Test();

		String testName = test.getName() + "_" + System.currentTimeMillis();

		clonedTest.setTestContent(test.getTestContent());
		clonedTest.setName(testName);
		clonedTest.setPodId(test.getPodId());
		clonedTest.setLastChangedTimestamp(System.currentTimeMillis());
		clonedTest.setActive(true);

		testRepository.save(clonedTest);

		Test dbTest = testRepository.getTestByName(testName);

		if (dbTest != null) {

			inputDataUtils.cloneInputDataByTestId(test.getId(), dbTest.getId());

			return dbTest;
		}
		return null;
	}

}
