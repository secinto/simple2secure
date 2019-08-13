package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Notification;
import com.simple2secure.api.model.Report;
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.TestResultRepository;

@Component
public class SearchUtils {

	private static Logger log = LoggerFactory.getLogger(SearchUtils.class);

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	TestResultRepository testResultRepository;

	public List<SearchResult> getAllSearchResults(String searchQuery) {
		List<SearchResult> searchResultList = new ArrayList<>();

		List<Notification> notifications = notificationRepository.getBySearchQuery(searchQuery);

		if (notifications != null) {
			log.info("Found {} notifications for search query: {}", notifications.size(), searchQuery);
			SearchResult sr = new SearchResult(notifications, "Notification");
			searchResultList.add(sr);
		}

		List<Report> reports = reportRepository.getBySearchQuery(searchQuery);

		if (reports != null) {
			log.info("Found {} osquery reports for search query: {}", reports.size(), searchQuery);
			SearchResult sr = new SearchResult(reports, "OSQuery Reports");
			searchResultList.add(sr);
		}

		List<NetworkReport> networkReports = networkReportRepository.getBySearchQuery(searchQuery);

		if (networkReports != null) {
			log.info("Found {} network reports for search query: {}", networkReports.size(), searchQuery);
			SearchResult sr = new SearchResult(networkReports, "Network Reports");
			searchResultList.add(sr);
		}

		List<TestResult> testResults = testResultRepository.getBySearchQuery(searchQuery);

		if (testResults != null) {
			log.info("Found {} test results for search query: {}", testResults.size(), searchQuery);
			SearchResult sr = new SearchResult(testResults, "Test Results");
			searchResultList.add(sr);
		}

		return searchResultList;
	}

}
