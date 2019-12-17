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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.NetworkReportDTO;
import com.simple2secure.api.dto.OsQueryReportDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputName;
import com.simple2secure.portal.validation.model.ValidInputPage;
import com.simple2secure.portal.validation.model.ValidInputReport;
import com.simple2secure.portal.validation.model.ValidInputSize;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.REPORT_API)
public class ReportController extends BaseUtilsProvider {
	static Logger log = LoggerFactory.getLogger(ReportController.class);

	@ValidRequestMapping(
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('DEVICE')")
	public ResponseEntity<OsQueryReport> saveReport(@RequestBody OsQueryReport report, @ServerProvidedValue ValidInputLocale locale) {
		if (report != null) {
			reportsRepository.save(report);
			return new ResponseEntity<>(report, HttpStatus.OK);
		}
		log.error("Error occured while saving report");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_report", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReportDTO> getReportsByContextIdAndPagination(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(contextId.getValue())) {

			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {

				List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, false);

				if (devices != null) {
					log.debug("Loading OSQuery reports for contextId {}", contextId.getValue());

					List<String> deviceIds = portalUtils.extractIdsFromObjects(devices);

					OsQueryReportDTO reportDto = new OsQueryReportDTO();

					reportDto = reportsRepository.getReportsByDeviceIdWithPagination(deviceIds, page.getValue(), size.getValue());

					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while retrieving reports for context {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/groups",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReportDTO> getReportsByGroupIdsAndPagination(@RequestBody List<CompanyGroup> groups,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {
		if (groups != null) {

			List<String> groupIds = portalUtils.extractIdsFromObjects(groups);

			if (groupIds != null && !groupIds.isEmpty()) {
				List<Device> devices = deviceUtils.getAllDevicesByGroupIds(groupIds);
				List<String> deviceIds = portalUtils.extractIdsFromObjects(devices);
				if (deviceIds != null) {
					OsQueryReportDTO reportDto = new OsQueryReportDTO();
					reportDto = reportsRepository.getReportsByDeviceIdWithPagination(deviceIds, page.getValue(), size.getValue());
					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while retrieving reports for groups");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/devices",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReportDTO> getReportsByDeviceIdsAndPagination(@RequestBody List<Device> devices,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {
		if (devices != null) {

			List<String> deviceIds = portalUtils.extractIdsFromObjects(devices);

			if (deviceIds != null && !deviceIds.isEmpty()) {
				OsQueryReportDTO reportDto = new OsQueryReportDTO();
				reportDto = reportsRepository.getReportsByDeviceIdWithPagination(deviceIds, page.getValue(), size.getValue());
				return new ResponseEntity<>(reportDto, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving reports for groups");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/report")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReport> getReportById(@PathVariable ValidInputReport reportId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(reportId.getValue())) {
			OsQueryReport report = reportsRepository.find(reportId.getValue());
			if (report != null) {
				return new ResponseEntity<>(report, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with id {}", reportId.getValue());
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/device")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<GraphReport>> getReportsByName(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputName name,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(name.getValue()) && !Strings.isNullOrEmpty(deviceId.getValue())) {
			List<GraphReport> reports = reportUtils.prepareReportsForGraph(deviceId.getValue(), name.getValue());
			if (reports != null) {
				return new ResponseEntity<>(reports, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with name {}", name);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/network",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('DEVICE')")
	public ResponseEntity<NetworkReport> saveNetworkReport(@RequestBody NetworkReport networkReport,
			@ServerProvidedValue ValidInputLocale locale) {
		if (networkReport != null) {
			networkReportRepository.save(networkReport);
			return new ResponseEntity<>(networkReport, HttpStatus.OK);
		}
		log.error("Error occured while saving network report");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_saving_report", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/network")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReportDTO> getNetworkReportsByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId.getValue());
				if (groups != null) {

					log.debug("Loading network reports for contextId {0}", contextId.getValue());

					List<String> groupIds = portalUtils.extractIdsFromObjects(groups);

					NetworkReportDTO reportDto = new NetworkReportDTO();

					reportDto = networkReportRepository.getReportsByGroupId(groupIds, size.getValue(), page.getValue());

					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
		}
		log.error("Error occured while retrieving network reports for context id {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/network",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReport> deleteNetworkReport(@PathVariable ValidInputReport reportId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(reportId.getValue())) {

			NetworkReport report = networkReportRepository.find(reportId.getValue());
			if (report != null) {
				networkReportRepository.delete(report);
				return new ResponseEntity<>(report, HttpStatus.OK);
			}
		}
		log.error("Error occured while deleting network report with id {}", reportId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/report/network/name",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReport>> getNetworkReportsByName(@RequestBody String name,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(name)) {
			List<NetworkReport> reports = networkReportRepository.getReportsByName(name);
			if (reports != null) {
				return new ResponseEntity<>(reports, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with name {}", name);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("report_not_found", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(
			value = "/delete/selected",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<OsQueryReport>> deleteSelectedReports(@RequestBody List<OsQueryReport> queryReports,
			@ServerProvidedValue ValidInputLocale locale) {
		if (queryReports != null) {
			for (OsQueryReport queryReport : queryReports) {
				OsQueryReport dbReport = reportsRepository.find(queryReport.getId());
				if (dbReport != null) {
					reportsRepository.delete(dbReport);
				}
			}
			return new ResponseEntity<>(queryReports, HttpStatus.OK);
		}
		log.error("Error occured while deleting selected network reports!");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("no_reports_provided", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}
