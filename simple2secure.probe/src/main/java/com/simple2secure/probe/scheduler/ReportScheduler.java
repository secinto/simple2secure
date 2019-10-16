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
package com.simple2secure.probe.scheduler;

import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Report;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.rest.RESTUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;

public class ReportScheduler extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(ReportScheduler.class);

	public ReportScheduler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if (ProbeConfiguration.isAPIAvailable()) {
			log.info("Sending reports to the server!");
			sendReportsToServer();
			log.info("Sending network reports to the server!");
			sendNetworkReportsToServer();
		}
	}

	/**
	 * This function sends the {@link Report} to the server updates the sent value and stores it in the database
	 *
	 * @param report
	 */
	private void sendReport(Report report) {
		// Do not send during license checking because the access token can be changed!
		if (!ProbeConfiguration.isCheckingLicense) {
			if (Strings.isNullOrEmpty(report.getDeviceId())) {
				report.setDeviceId(ProbeConfiguration.probeId);
			}
			if (Strings.isNullOrEmpty(report.getGroupId())) {
				report.setGroupId(ProbeConfiguration.groupId);
			}
			report.setSent(true);
			log.debug("Sending report {} with timestamp {} to the API.", report.getQuery(), report.getQueryTimestamp());
			RESTUtils.sendPost(LoadedConfigItems.getInstance().getReportsAPI(), report, ProbeConfiguration.authKey);
			DBUtil.getInstance().merge(report);
		}
	}

	/**
	 * This function sends the {@link NetworkReport} to the server updates the sent value and stores it in the database
	 *
	 * @param report
	 */
	private void sendNetworkReport(NetworkReport report) {
		// Do not send during license checking because the access token can be changed!
		if (!ProbeConfiguration.isCheckingLicense) {
			if (Strings.isNullOrEmpty(report.getDeviceId())) {
				report.setDeviceId(ProbeConfiguration.probeId);
			}
			if (Strings.isNullOrEmpty(report.getGroupId())) {
				report.setGroupId(ProbeConfiguration.groupId);
			}
			report.setSent(true);
			log.info("Sending network report to the server with id: " + report.getId());
			RESTUtils.sendPost(LoadedConfigItems.getInstance().getReportsAPI() + "/network", report, ProbeConfiguration.authKey);
			DBUtil.getInstance().merge(report);
		}

	}

	/**
	 * This function retrieves all {@link Report} objects from the database where sent tag is false.
	 */
	private void sendReportsToServer() {
		List<Report> reports = DBUtil.getInstance().findByFieldName("isSent", false, Report.class);
		if (reports != null) {
			for (Report report : reports) {
				sendReport(report);
			}
		}
	}

	/**
	 * This function retrieves all {@link NetworkReport} objects from the database where sent tag is false.
	 */
	private void sendNetworkReportsToServer() {
		List<NetworkReport> networkReports = DBUtil.getInstance().findByFieldName("sent", false, NetworkReport.class);
		if (networkReports != null) {
			for (NetworkReport networkReport : networkReports) {
				sendNetworkReport(networkReport);
			}
		}
	}

}