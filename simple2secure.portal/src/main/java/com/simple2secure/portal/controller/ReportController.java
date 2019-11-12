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
package com.simple2secure.portal.controller;

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
import com.simple2secure.api.dto.NetworkReportDTO;
import com.simple2secure.api.dto.ReportDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.GraphReport;
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
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.ReportUtils;

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
	ReportUtils reportUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	RestTemplate restTemplate = new RestTemplate();

	private static Logger log = LoggerFactory.getLogger(ReportController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('DEVICE')")
	public ResponseEntity<Report> saveReport(@RequestBody Report report, @RequestHeader("Accept-Language") String locale) {
		if (report != null) {
			reportsRepository.save(report);
			return new ResponseEntity<>(report, HttpStatus.OK);
		}
		log.error("Error occured while saving report");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_report", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}/{page}/{size}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<ReportDTO> getReportsByContextIdAndPagination(@PathVariable("contextId") String contextId,
			@PathVariable("page") int page, @PathVariable("size") int size, @RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(contextId)) {

			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId);

				if (groups != null) {
					log.debug("Loading OSQuery reports for contextId {0}", contextId);

					List<String> groupIds = portalUtils.extractIdsFromObjects(groups);

					ReportDTO reportDto = new ReportDTO();

					reportDto = reportsRepository.getReportsByGroupId(groupIds, page, size);

					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while retrieving reports for context {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Report> getReportByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(id)) {
			Report report = reportsRepository.find(id);
			if (report != null) {
				return new ResponseEntity<>(report, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with id {}", id);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/report/name", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<GraphReport>> getReportsByName(@RequestBody String name, @RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(name)) {
			List<GraphReport> reports = reportUtils.prepareReportsForGraph(name);
			if (reports != null) {
				return new ResponseEntity<>(reports, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with name {}", name);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Report> deleteReport(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(id)) {
			Report report = reportsRepository.find(id);
			if (report != null) {
				reportsRepository.delete(report);
				return new ResponseEntity<>(report, HttpStatus.OK);
			}
		}
		log.error("Error occured while deleting report with id {}", id);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/network", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAuthority('DEVICE')")
	public ResponseEntity<NetworkReport> saveNetworkReport(@RequestBody NetworkReport networkReport,
			@RequestHeader("Accept-Language") String locale) {
		if (networkReport != null) {
			networkReportRepository.save(networkReport);
			return new ResponseEntity<>(networkReport, HttpStatus.OK);
		}
		log.error("Error occured while saving network report");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_report", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/network/{contextId}/{page}/{size}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReportDTO> getNetworkReportsByContextId(@PathVariable("contextId") String contextId,
			@PathVariable("page") int page, @PathVariable("size") int size, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
				if (groups != null) {

					log.debug("Loading network reports for contextId {0}", contextId);

					List<String> groupIds = portalUtils.extractIdsFromObjects(groups);

					NetworkReportDTO reportDto = new NetworkReportDTO();

					reportDto = networkReportRepository.getReportsByGroupId(groupIds, size, page);

					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while retrieving network reports for context id {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/network/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReport> deleteNetworkReport(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(id)) {

			NetworkReport report = networkReportRepository.find(id);
			if (report != null) {
				networkReportRepository.delete(report);
				return new ResponseEntity<>(report, HttpStatus.OK);
			}
		}
		log.error("Error occured while deleting network report with id {}", id);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/report/network/name", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReport>> getNetworkReportsByName(@RequestBody String name,
			@RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(name)) {
			List<NetworkReport> reports = networkReportRepository.getReportsByName(name);
			if (reports != null) {
				return new ResponseEntity<>(reports, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with name {}", name);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/report/network/geo", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReportDTO>> getNetworkReportsForGeoLocation(@RequestHeader("Accept-Language") String locale) {
		List<NetworkReportDTO> reports = reportUtils.prepareNetworkReports();
		if (reports != null) {
			return new ResponseEntity<>(reports, HttpStatus.OK);
		}
		log.error("Error occured while retrieving reports for geolocation");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale)), HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/selected", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Report>> deleteSelectedReports(@RequestBody List<Report> queryReports,
			@RequestHeader("Accept-Language") String locale) {
		if (queryReports != null) {
			for (Report queryReport : queryReports) {
				Report dbReport = reportsRepository.find(queryReport.getId());
				if (dbReport != null) {
					reportsRepository.delete(dbReport);
				}
			}
			return new ResponseEntity<>(queryReports, HttpStatus.OK);
		}
		log.error("Error occured while deleting selected network reports!");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale)), HttpStatus.NOT_FOUND);
	}
}
