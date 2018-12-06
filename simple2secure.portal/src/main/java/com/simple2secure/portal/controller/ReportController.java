/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Report;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	@Autowired
	ReportRepository reportsRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	RestTemplate restTemplate = new RestTemplate();

	private static Logger log = LoggerFactory.getLogger(ReportController.class);

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('PROBE')")
	public ResponseEntity<Report> saveReport(@RequestBody Report report, @RequestHeader("Accept-Language") String locale) {
		reportsRepository.save(report);
		return new ResponseEntity<Report>(report, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Report>> getReportsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId);

				if (groups != null) {
					log.debug("Loading OSQuery reports for contextId {0}", contextId);
					List<Report> reportsList = new ArrayList<Report>();
					for (CompanyGroup group : groups) {
						reportsList.addAll(reportsRepository.getReportsByGroupId(group.getId()));
					}
					return new ResponseEntity<List<Report>>(reportsList, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Report> getReportByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		Report report = reportsRepository.find(id);
		if (report == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Report>(report, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Report> deleteReport(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		if (Strings.isNullOrEmpty(id)) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			Report report = reportsRepository.find(id);
			if (report != null) {
				reportsRepository.delete(report);
				return new ResponseEntity<Report>(report, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
						HttpStatus.NOT_FOUND);
			}
		}
	}

	@RequestMapping(value = "/network", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('PROBE')")
	public ResponseEntity<NetworkReport> saveNetworkReport(@RequestBody NetworkReport networkReport,
			@RequestHeader("Accept-Language") String locale) {
		networkReportRepository.save(networkReport);
		return new ResponseEntity<NetworkReport>(networkReport, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/network/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReport>> getNetworkReportsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
				if (groups != null) {
					log.debug("Loading network reports for contextId {0}", contextId);
					List<NetworkReport> reportsList = new ArrayList<NetworkReport>();
					for (CompanyGroup group : groups) {
						reportsList.addAll(networkReportRepository.getReportsByGroupId(group.getId()));
					}
					return new ResponseEntity<List<NetworkReport>>(reportsList, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/network/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReport> deleteNetworkReport(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		if (Strings.isNullOrEmpty(id)) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			NetworkReport report = networkReportRepository.find(id);
			if (report != null) {
				networkReportRepository.delete(report);
				return new ResponseEntity<NetworkReport>(report, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
						HttpStatus.NOT_FOUND);
			}
		}
	}

}
