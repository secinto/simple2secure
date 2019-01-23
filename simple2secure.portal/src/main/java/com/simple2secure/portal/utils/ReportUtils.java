package com.simple2secure.portal.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.model.CityResponse;
import com.simple2secure.api.dto.NetworkReportDTO;
import com.simple2secure.api.model.GraphReport;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Report;
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
							JSONArray jsonArrayQueryResult = new JSONArray(report.getQueryResult());
							if (jsonArrayQueryResult != null) {

								// Fri Jan 18 08:24:33 CET 2019
								DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US);
								Date date = format.parse(report.getQueryTimestamp());

								graphReports.add(new GraphReport(report.getId(), report.getQuery(), jsonArrayQueryResult.length(), date.getTime()));
							}

						} catch (JSONException e) {
							log.error("Error occured while trying to parse string to jsonArray: {}", e);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		return graphReports;
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public List<NetworkReportDTO> prepareNetworkReports() {
		List<NetworkReport> networkReports = networkReportRepository.getReportsByName("common-stats");
		List<NetworkReportDTO> preparedReports = new ArrayList<>();
		for (NetworkReport report : networkReports) {

			try {
				JSONObject reportContent = new JSONObject(report.getStringContent());
				JSONObject sourceIP = reportContent.getJSONObject("Source IP");
				JSONObject destinationIP = reportContent.getJSONObject("Destination IP");
				JSONArray valuesSourceIP = sourceIP.names();
				JSONArray valuesDestIP = destinationIP.names();

				int arraySize = valuesSourceIP.length();

				if (valuesDestIP.length() < arraySize) {
					arraySize = valuesDestIP.length();
				}
				// JSONArray destIPvalues =
				if (valuesSourceIP != null) {
					for (int valuesIndex = 0; valuesIndex < arraySize; valuesIndex++) {
						String sourceIp = valuesSourceIP.getString(valuesIndex);
						String destIp = valuesDestIP.getString(valuesIndex);
						CityResponse response = iptoGeoUtils.convertIPtoGeoLocation("87.243.178.234");

						if (response != null) {
							NetworkReportDTO reportDTO = new NetworkReportDTO(response.getLocation().getLatitude(), response.getLocation().getLongitude(),
									report.getId(), report.getGroupId(), report.getStartTime(), report.getProcessorName());
							preparedReports.add(reportDTO);
						}
					}
				}
			} catch (JSONException e) {
				log.error(e.getMessage());
			}
		}
		return preparedReports;
	}
}
