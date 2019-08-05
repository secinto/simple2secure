/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.PodDTO;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PodUtils;
import com.simple2secure.portal.utils.TestUtils;

@RestController
@RequestMapping("/api/pod")
public class PodController {

	public static final Logger log = LoggerFactory.getLogger(PodController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	PodUtils podUtils;

	@Autowired
	TestUtils testUtils;

	/**
	 * This function returns all pods according to the contextId
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<PodDTO>> getPodsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<PodDTO> pods = podUtils.getAllPodsFromCurrentContextWithTests(context);

				if (pods != null) {
					return new ResponseEntity<List<PodDTO>>(pods, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while retrieving pods for contextId {}", contextId);

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function retrieves the tests according to the podId, if no tests are found then it checks if there are tests according to the
	 * hostname.
	 *
	 * @param podId
	 * @param hostname
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@RequestMapping(value = "/config/{podId}/{hostname}", method = RequestMethod.GET)
	public ResponseEntity<List<Test>> checkConfiguration(@PathVariable("podId") String podId, @PathVariable("hostname") String hostname)
			throws ItemNotFoundRepositoryException {

		List<Test> test = testRepository.getByPodId(podId);
		CompanyLicensePrivate podLicense = licenseRepository.findByPodId(podId);

		if (podLicense != null) {
			podLicense.setLastOnlineTimestamp(System.currentTimeMillis());
			licenseRepository.update(podLicense);
		}

		if (test == null || test.isEmpty()) {
			test = testRepository.getByHostname(hostname);
		}

		return new ResponseEntity<List<Test>>(test, HttpStatus.OK);

	}

	/**
	 * This function retrieves the scheduled tests from the list of the scheduled tests and returns them to the pod.
	 *
	 * @param podId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/scheduledTests/{podId}", method = RequestMethod.GET, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('POD')")
	public ResponseEntity<List<TestRun>> getScheduledTests(@PathVariable("podId") String podId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		CompanyLicensePrivate podLicense = licenseRepository.findByPodId(podId);

		if (podLicense != null) {
			podLicense.setLastOnlineTimestamp(System.currentTimeMillis());
			licenseRepository.update(podLicense);
			return testUtils.getScheduledTestsByPodId(podId, locale);
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_tests", locale)),
				HttpStatus.NOT_FOUND);

	}

}
