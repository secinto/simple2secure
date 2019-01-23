package com.simple2secure.portal.controller;

import java.io.IOException;
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
import com.simple2secure.api.dto.ToolDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.TestCase;
import com.simple2secure.api.model.TestCaseSequence;
import com.simple2secure.api.model.TestCaseTemplate;
import com.simple2secure.api.model.Tool;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.TestTemplateRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.TestUtils;
import com.simple2secure.portal.utils.ToolUtils;

import io.kubernetes.client.ApiException;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

	@Autowired
	ToolRepository toolRepository;

	@Autowired
	TestTemplateRepository testTemplateRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	ToolUtils toolUtils;

	@Autowired
	TestUtils testUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<ToolDTO>> getToolsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);

			if (context != null) {
				return toolUtils.getKubernetesTools(locale, contextId);
			}
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{toolId}/run", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestCaseSequence> runCommand(@RequestBody TestCase test, @PathVariable("toolId") String toolId,
			@RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {

		if (test != null && !Strings.isNullOrEmpty(test.getToolId())) {
			Tool tool = toolRepository.find(test.getToolId());

			if (tool != null) {
				return testUtils.addTestCaseToTheList(test, locale);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_adding_test_sequence", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/{templateId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestCaseTemplate> deleteTemplate(@PathVariable("templateId") String templateId,
			@RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {

		if (!Strings.isNullOrEmpty(templateId)) {
			TestCaseTemplate template = testTemplateRepository.find(templateId);
			if (template != null) {
				testTemplateRepository.delete(template);
				return new ResponseEntity<TestCaseTemplate>(template, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_template", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/test/{testId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestCase> deleteTest(@PathVariable("testId") String testId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {

		if (!Strings.isNullOrEmpty(testId)) {
			return testUtils.deleteTestCaseAndDependencies(testId, locale);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_template", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/updateTemplate", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestCaseTemplate> updateTestTemplate(@RequestBody TestCaseTemplate template, @PathVariable("toolId") String toolId,
			@RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {

		if (template != null && !Strings.isNullOrEmpty(template.getToolId())) {
			Tool tool = toolRepository.find(template.getToolId());

			if (tool != null) {
				if (!Strings.isNullOrEmpty(template.getId())) {
					testTemplateRepository.update(template);
				} else {
					testTemplateRepository.save(template);
				}
				return new ResponseEntity<TestCaseTemplate>(template, HttpStatus.OK);
			}

		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_adding_test_template", locale)),
				HttpStatus.NOT_FOUND);
	}

}
