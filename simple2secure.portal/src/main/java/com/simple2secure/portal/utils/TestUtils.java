package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.simple2secure.api.dto.TestResultDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class TestUtils {

	private static Logger log = LoggerFactory.getLogger(TestUtils.class);

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	/**
	 * This function saves the Test Result which has been executed by the pod
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<TestResult> saveTestResult(TestResult testResult, String locale) {
		if (testResult != null && !Strings.isNullOrEmpty(locale)) {
			if (!Strings.isNullOrEmpty(testResult.getLicenseId()) && !Strings.isNullOrEmpty(testResult.getGroupId())) {
				testResultRepository.save(testResult);
				return new ResponseEntity<TestResult>(testResult, HttpStatus.OK);
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_result", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the test results by context id
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
					List<TestResult> grpResults = testResultRepository.getByGroupId(group.getId());

					for (TestResult result : grpResults) {
						if (result != null) {
							TestResultDTO testResultDTO = new TestResultDTO(result, group);
							results.add(testResultDTO);
						}
					}
				}
				if (results != null) {
					return new ResponseEntity<List<TestResultDTO>>(results, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_result", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function checks if the test result with the provide id exists, and deletes it accordingly.
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

}
