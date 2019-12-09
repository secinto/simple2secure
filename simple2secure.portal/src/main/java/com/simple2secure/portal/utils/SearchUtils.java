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

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Notification;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;

@Component
public class SearchUtils {

	private static Logger log = LoggerFactory.getLogger(SearchUtils.class);

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	OsQueryReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestRunRepository testRunRepository;

	/**
	 * This function searches notification, testResult, report and networkReport tables in the database by the full text search for the
	 * content with the provided searchQuery and contextId
	 *
	 * @param searchQuery
	 * @param contextId
	 * @return
	 */
	public List<SearchResult> getAllSearchResults(String searchQuery, String contextId) {
		List<SearchResult> searchResultList = new ArrayList<>();

		List<Notification> notifications = notificationRepository.getBySearchQuery(searchQuery, contextId, false);

		if (notifications != null) {
			log.info("Found {} notifications for search query: {}", notifications.size(), searchQuery);
			SearchResult sr = new SearchResult(notifications, "Notification");
			searchResultList.add(sr);
		}

		List<CompanyGroup> groups = groupRepository.findByContextId(contextId);

		List<OsQueryReport> queryReportList = new ArrayList<>();
		List<NetworkReport> networkReportList = new ArrayList<>();
		List<TestResult> testResultList = new ArrayList<>();

		if (groups != null) {
			for (CompanyGroup group : groups) {

				List<OsQueryReport> reports = reportRepository.getSearchQueryByGroupId(searchQuery, group.getId());

				if (reports != null) {
					queryReportList.addAll(reports);
					log.info("Found {} osquery reports for search query: {}", reports.size(), searchQuery);

				}

				List<NetworkReport> networkReports = networkReportRepository.getSearchQueryByGroupId(searchQuery, group.getId());

				if (networkReports != null) {
					networkReportList.addAll(networkReports);
					log.info("Found {} network reports for search query: {}", networkReports.size(), searchQuery);

				}
			}
		}

		SearchResult sr = new SearchResult(queryReportList, "OSQuery Reports");
		searchResultList.add(sr);

		sr = new SearchResult(networkReportList, "Network Reports");
		searchResultList.add(sr);

		List<TestRun> tests = testRunRepository.getByContextId(contextId);

		if (tests != null) {
			for (TestRun testRun : tests) {
				List<TestResult> testResults = testResultRepository.getSearchQueryByTestRunId(searchQuery, testRun.getId());
				if (testResults != null) {
					log.info("Found {} test results for search query: {}", testResults.size(), searchQuery);
					testResultList.addAll(testResults);
				}
			}
		}

		sr = new SearchResult(testResultList, "Test Results");
		searchResultList.add(sr);

		return searchResultList;
	}

}
