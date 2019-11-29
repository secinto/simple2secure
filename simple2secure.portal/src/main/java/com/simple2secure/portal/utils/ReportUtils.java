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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.Report;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ReportRepository;

@Component
public class ReportUtils {

	private static Logger log = LoggerFactory.getLogger(ReportUtils.class);

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	IpToGeoUtils iptoGeoUtils;

	/**
	 * This function prepares the Report for the graph in the web. It parses only the necessary information so that we ignore the long queues.
	 *
	 * @param queryName
	 * @return
	 */
	public List<GraphReport> prepareReportsForGraph(String deviceId, String queryName) {
		int currentPage = 0;
		int size = ConfigItems.DEFAULT_VALUE_SIZE;
		long maxPages = reportRepository.getPagesForReportsByDeviceAndName(deviceId, queryName);
		List<GraphReport> graphReports = new ArrayList<>();
		while (currentPage <= maxPages) {
			List<Report> reports = reportRepository.getReportsByDeviceAndName(deviceId, queryName, currentPage, size);
			if (reports != null) {
				for (Report report : reports) {
					if (report != null) {
						if (report.getQueryResult() != null) {
							try {
								JsonNode node = JSONUtils.fromString(report.getQueryResult());

								int length = report.getQueryResult().length();
								if (node != null) {
									length = node.size();
								}
								graphReports.add(new GraphReport(report.getId(), report.getQuery(), length, report.getQueryTimestamp().getTime()));

							} catch (Exception e) {
								log.error("Error occured while trying to parse string to jsonArray: {}", e);
								e.printStackTrace();
							}
						}
					}
				}
			}
			currentPage++;
		}

		return graphReports;
	}

	/**
	 * This function prepares the Report for the graph in the web. It parses only the necessary information so that we ignore the long queues.
	 *
	 * @param queryName
	 * @return
	 */
	public List<GraphReport> prepareReportsForGraph(String queryName) {
		int currentPage = 0;
		int size = StaticConfigItems.DEFAULT_VALUE_SIZE;
		long maxPages = reportRepository.getPagesForReportsByName(queryName);
		List<GraphReport> graphReports = new ArrayList<>();
		while (currentPage < maxPages) {
			List<Report> reports = reportRepository.getReportsByName(queryName, currentPage, size);
			if (reports != null) {
				for (Report report : reports) {
					if (report != null) {
						if (report.getQueryResult() != null) {
							try {
								JsonNode node = JSONUtils.fromString(report.getQueryResult());

								int length = report.getQueryResult().length();
								if (node != null) {
									length = node.size();
								}
								graphReports.add(new GraphReport(report.getId(), report.getQuery(), length, report.getQueryTimestamp().getTime()));

							} catch (Exception e) {
								log.error("Error occured while trying to parse string to jsonArray: {}", e);
								e.printStackTrace();
							}
						}
					}
				}
			}
			currentPage++;
		}

		return graphReports;
	}

	public Report getDifferencesForIdWithPrevious(String id) {
		Report report = reportRepository.find(id);
		if (report != null) {
			List<Report> lastReports = reportRepository.getLastReportsFromTimeStampAndName(report.getQueryTimestamp(), report.getName());
		}
		return report;
	}
}
