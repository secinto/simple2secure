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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReportUtils extends BaseServiceProvider {

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	PortalUtils portalUtils;

	/**
	 * This function prepares the Report for the graph in the web. It parses only the necessary information so that we ignore the long queues.
	 *
	 * @param queryName
	 * @return
	 */
	public List<GraphReport> prepareReportsForGraph(ObjectId deviceId, String queryName) {
		int currentPage = 0;
		int size = StaticConfigItems.DEFAULT_VALUE_SIZE;
		long maxPages = reportsRepository.getPagesForReportsByDeviceAndName(deviceId, queryName);
		List<GraphReport> graphReports = new ArrayList<>();
		while (currentPage <= maxPages) {
			List<OsQueryReport> reports = reportsRepository.getReportsByDeviceAndName(deviceId, queryName, currentPage, size);
			if (reports != null) {
				for (OsQueryReport report : reports) {
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
	 * This functions returns the number of the executed OSQueries
	 *
	 * @param context
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public int countExecutedQueries(Context context) throws ItemNotFoundRepositoryException {
		int size = 0;
		if (context != null) {
			List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, false);
			List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);
			if (deviceIds != null && deviceIds.size() > 0) {
				List<OsQueryReport> reports = reportsRepository.getReportsByDeviceId(deviceIds);
				if (reports != null) {
					size = reports.size();
				}
			}
		}
		return size;
	}
}
