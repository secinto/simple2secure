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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Notification;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SearchUtils extends BaseServiceProvider {

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	PortalUtils portalUtils;

	/**
	 * This function searches notification, testResult, report and networkReport tables in the database by the full text search for the
	 * content with the provided searchQuery and contextId
	 *
	 * @param searchQuery
	 * @param contextId
	 * @return
	 */
	public List<SearchResult> getAllSearchResults(String searchQuery, Context context) {

		List<SearchResult> searchResultList = new ArrayList<>();
		List<TestResult> testResultList = new ArrayList<>();

		List<Device> devices = deviceUtils.getAllDevicesFromCurrentContext(context, false);

		List<String> deviceIds = portalUtils.extractIdsFromObjects(devices);

		List<Notification> notifications = notificationRepository.getBySearchQuery(searchQuery, context.getId(), false);

		if (notifications != null) {
			log.info("Found {} notifications for search query: {}", notifications.size(), searchQuery);
			searchResultList.add(new SearchResult(notifications, "Notification"));
		}

		List<OsQueryReport> reports = reportsRepository.getSearchQueryByDeviceIds(searchQuery, deviceIds);

		if (reports != null) {
			log.info("Found {} osquery reports for search query: {}", reports.size(), searchQuery);
			searchResultList.add(new SearchResult(reports, "OSQuery Reports"));

		}

		List<NetworkReport> networkReports = networkReportRepository.getSearchQueryByDeviceIds(searchQuery, deviceIds);

		if (networkReports != null) {
			log.info("Found {} network reports for search query: {}", networkReports.size(), searchQuery);
			searchResultList.add(new SearchResult(networkReports, "Network Reports"));
		}

		List<TestRun> tests = testRunRepository.getByContextId(context.getId());

		if (tests != null) {
			for (TestRun testRun : tests) {
				List<TestResult> testResults = testResultRepository.getSearchQueryByTestRunId(searchQuery, testRun.getId());
				if (testResults != null) {
					log.info("Found {} test results for search query: {}", testResults.size(), searchQuery);
					testResultList.addAll(testResults);
				}
			}
		}
		searchResultList.add(new SearchResult(testResultList, "Test Results"));

		return searchResultList;
	}

}
