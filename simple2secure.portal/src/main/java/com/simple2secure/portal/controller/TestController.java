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
import com.simple2secure.api.model.TestResult;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
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
	TestUtils testUtils;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/saveTestResult", method = RequestMethod.POST, consumes = "application/json")
	// @PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<TestResult> saveTestResult(@RequestBody TestResult testResult, @RequestHeader("Accept-Language") String locale) {

		if (testResult != null) {
			if (!Strings.isNullOrEmpty(testResult.getLicenseId()) && !Strings.isNullOrEmpty(testResult.getGroupId())
					&& testResult.getResult() != null) {
				return testUtils.saveTestResult(testResult, locale);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test_result", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/testresult/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<TestResultDTO>> getTestResultByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			return testUtils.getTestResultByContextId(contextId, locale);
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_tests", locale)),
				HttpStatus.NOT_FOUND);
	}

}
