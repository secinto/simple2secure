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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.geoip2.model.CityResponse;
import com.simple2secure.api.dto.NetworkReportDTO;
import com.simple2secure.api.model.Coordinates;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.PacketInfo;
import com.simple2secure.api.model.Report;
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
	public List<GraphReport> prepareReportsForGraph(String queryName) {

		List<Report> reports = reportRepository.getReportsByName(queryName);
		List<GraphReport> graphReports = new ArrayList<>();

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

		return graphReports;
	}

	/**
	 * This function retrieves all network reports for the common-stats processor, iterates through each packet which has been saved in the
	 * report and converts an IP Address to the GeoLocation.
	 *
	 * @return
	 * @throws IOException
	 */
	public List<NetworkReportDTO> prepareNetworkReports() {
		List<NetworkReport> networkReports = networkReportRepository.getReportsByName("common-stats");
		List<NetworkReportDTO> preparedReports = new ArrayList<>();
		for (NetworkReport report : networkReports) {

			List<Coordinates> coordinates = new ArrayList<>();
			if (report.getIpPairs() != null) {

				for (PacketInfo info : report.getIpPairs()) {

					String destIp = info.getDestination_ip();
					String srcIp = info.getSource_ip();

					if (destIp.contains("192.168")) {
						destIp = "87.243.178.234";
					}

					if (srcIp.contains("192.168")) {
						srcIp = "87.243.178.234";
					}

					if (!srcIp.equals(destIp)) {

						CityResponse responseSrcIp = iptoGeoUtils.convertIPtoGeoLocation(srcIp.replaceAll("/", ""));
						CityResponse responsedestIp = iptoGeoUtils.convertIPtoGeoLocation(destIp.replaceAll("/", ""));

						if (responseSrcIp != null && responsedestIp != null) {

							Coordinates coord = new Coordinates(responseSrcIp.getLocation().getLatitude(), responseSrcIp.getLocation().getLongitude(),
									responsedestIp.getLocation().getLatitude(), responsedestIp.getLocation().getLongitude());

							if (!coordinates.contains(coord)) {
								coordinates.add(coord);
							}
						}
					}
				}

				NetworkReportDTO reportDTO = new NetworkReportDTO(coordinates, report.getId(), report.getGroupId(), report.getStartTime(),
						report.getProcessorName());

				preparedReports.add(reportDTO);

			}
		}
		return preparedReports;
	}
}
