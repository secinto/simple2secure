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

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.NetworkReportDTO;
import com.simple2secure.api.dto.OsQueryReportDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.FactToCheckByRuleEngine;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.ReportType;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputName;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.REPORT_API)
@Slf4j
public class ReportController extends BaseUtilsProvider {

	@ValidRequestMapping(
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ROLE_DEVICE')")
	public ResponseEntity<OsQueryReport> saveReport(@RequestBody OsQueryReport report, @ServerProvidedValue ValidInputLocale locale) {
		if (report != null) {
			ObjectId reportId = reportsRepository.saveAndReturnId(report);
			factsToCheckRepository.save(new FactToCheckByRuleEngine(reportId, RuleFactType.OSQUERYREPORT, false));
			return new ResponseEntity<>(report, HttpStatus.OK);
		}
		log.error("Error occured while saving report");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_saving_report", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/groups",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReportDTO> getReportsByGroupIdsAndPagination(@RequestBody List<CompanyGroup> groups, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (groups != null) {

			List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
			if (groupIds != null && !groupIds.isEmpty()) {
				List<Device> devices = deviceUtils.getAllDevicesWithReportsByGroupId(groupIds, DeviceType.PROBE, ReportType.OSQUERY);
				if (devices != null) {
					List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);
					OsQueryReportDTO reportDto = new OsQueryReportDTO();
					
					if (deviceIds != null) {
						reportDto = reportsRepository.getReportsByDeviceIdWithPagination(deviceIds, page, size, filter);	
					}
					
					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
			throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports_group", locale.getValue()));
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/devices",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<OsQueryReportDTO> getReportsByDeviceIdsAndPagination(@RequestBody List<Device> devices, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {
		if (devices != null) {

			List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);

			if (deviceIds != null && !deviceIds.isEmpty()) {
				OsQueryReportDTO reportDto = new OsQueryReportDTO();
				reportDto = reportsRepository.getReportsByDeviceIdWithPagination(deviceIds, page, size, filter);
				return new ResponseEntity<>(reportDto, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving reports for groups");
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/device")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<GraphReport>> getReportsByName(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputName name,
			@ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(name.getValue()) && deviceId.getValue() != null) {
			List<GraphReport> reports = reportUtils.prepareReportsForGraph(deviceId.getValue(), name.getValue());
			if (reports != null) {
				return new ResponseEntity<>(reports, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving report with name {}", name);
		throw new ApiRequestException(messageByLocaleService.getMessage("report_not_found", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/network",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ROLE_DEVICE')")
	public ResponseEntity<NetworkReport> saveNetworkReport(@RequestBody NetworkReport networkReport,
			@ServerProvidedValue ValidInputLocale locale) {
		if (networkReport != null) {
			ObjectId networkReportId = networkReportRepository.saveAndReturnId(networkReport);
			factsToCheckRepository.save(new FactToCheckByRuleEngine(networkReportId, RuleFactType.NETWORKREPORT, false));
			return new ResponseEntity<>(networkReport, HttpStatus.OK);
		}
		log.error("Error occured while saving network report");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_saving_report", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/network/devices",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReportDTO> getNetworkReportsByDeviceIdsAndPagination(@RequestBody List<Device> devices, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {
		if (devices != null) {

			List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);

			if (deviceIds != null && !deviceIds.isEmpty()) {
				NetworkReportDTO reportDto = new NetworkReportDTO();
				reportDto = networkReportRepository.getReportsByDeviceIdWithPagination(deviceIds, page, size, filter);
				return new ResponseEntity<>(reportDto, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving reports for groups");
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/network/groups",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<NetworkReportDTO> getNetworkReportsByGroupIdsAndPagination(@RequestBody List<CompanyGroup> groups, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (groups != null) {

			List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
			if (groupIds != null && !groupIds.isEmpty()) {
				List<Device> devices = deviceUtils.getAllDevicesWithReportsByGroupId(groupIds, DeviceType.PROBE, ReportType.NETWORK);
				if (devices != null) {
					List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);
					NetworkReportDTO reportDto = new NetworkReportDTO();
					
					if (deviceIds != null) {
						reportDto = networkReportRepository.getReportsByDeviceIdWithPagination(deviceIds, page, size, filter);	
					}
					
					return new ResponseEntity<>(reportDto, HttpStatus.OK);
				}
			}
			throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports_group", locale.getValue()));
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
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
		log.error("Error occured while deleting selected osquery reports!");
		throw new ApiRequestException(messageByLocaleService.getMessage("no_reports_provided", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/network/delete/selected",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<NetworkReport>> deleteSelectedNetworkReports(@RequestBody List<NetworkReport> networkReports,
			@ServerProvidedValue ValidInputLocale locale) {
		if (networkReports != null) {
			for (NetworkReport networkReport : networkReports) {
				NetworkReport dbReport = networkReportRepository.find(networkReport.getId());
				if (dbReport != null) {
					networkReportRepository.delete(dbReport);
				}
			}
			return new ResponseEntity<>(networkReports, HttpStatus.OK);
		}
		log.error("Error occured while deleting selected network reports!");
		throw new ApiRequestException(messageByLocaleService.getMessage("no_reports_provided", locale.getValue()));
	}
}
