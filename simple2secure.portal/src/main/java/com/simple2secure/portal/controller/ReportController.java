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
import com.simple2secure.api.dto.ReportDTO;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Report;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class ReportController {

	@Autowired
	ReportRepository reportsRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	RestTemplate restTemplate = new RestTemplate();

	private static Logger log = LoggerFactory.getLogger(ReportController.class);

	/**
	 * This API returns JSON, with all reports listed in it.
	 *
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/reports", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<ReportDTO>> getAvailableReports(@RequestHeader("Accept-Language") String locale) {

		List<Report> reportList = reportsRepository.findAll();
		List<ReportDTO> reportListDTO = new ArrayList<>();

		if (reportList == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			for (Report report : reportList) {

				if (!Strings.isNullOrEmpty(report.getProbeId())) {
					CompanyLicense license = licenseRepository.findByProbeId(report.getProbeId());
					if (license != null) {
						User user = userRepository.findByUserID(license.getUserId());
						if (user != null) {
							ReportDTO reportDTO = new ReportDTO(user.getUsername(), report);
							reportListDTO.add(reportDTO);
						} else {
							log.error("There is no license with the provided userId");
						}

					} else {
						log.error("There is no license with the provided probeId");
					}
				} else {
					log.error("Report does not contain probe ID");
				}

			}

			if (reportListDTO == null || reportListDTO.isEmpty()) {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)),
						HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<List<ReportDTO>>(reportListDTO, HttpStatus.OK);
			}
		}
	}

	@RequestMapping(value = "/api/reports", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('PROBE')")
	public ResponseEntity<Report> saveReport(@RequestBody Report report, @RequestHeader("Accept-Language") String locale) {
		reportsRepository.save(report);
		return new ResponseEntity<Report>(report, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/reports/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Report>> getReportsByUserId(@PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {
		// TODO Auto-generated method stub
		List<Report> reportsList = reportsRepository.getAllReportsByUserID(userId);
		if (reportsList == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Report>>(reportsList, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/reports/report/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Report> getReportByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		Report report = reportsRepository.find(id);
		if (report == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Report>(report, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/reports/{id}", method = RequestMethod.DELETE)
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

	@RequestMapping(value = "/api/reports/network", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('PROBE')")
	public ResponseEntity<NetworkReport> saveNetworkReport(@RequestBody NetworkReport networkReport,
			@RequestHeader("Accept-Language") String locale) {
		networkReportRepository.save(networkReport);
		return new ResponseEntity<NetworkReport>(networkReport, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/reports/network/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReport>> getNetworkReportsByUserId(@PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {
		// TODO Auto-generated method stub
		List<NetworkReport> reportsList = networkReportRepository.getReportsByUserID(userId);
		if (reportsList == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<NetworkReport>>(reportsList, HttpStatus.OK);
	}

}
