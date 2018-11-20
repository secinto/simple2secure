package com.simple2secure.probe.scheduler;

import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Report;
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
			if (Strings.isNullOrEmpty(report.getProbeId())) {
				report.setProbeId(ProbeConfiguration.probeId);
			}
			report.setSent(true);
			log.debug("Sending report {} with timestamp {} to the API.", report.getQuery(), report.getQueryTimestamp());
			RESTUtils.sendPost(ProbeConfiguration.getInstance().getLoadedConfigItems().getReportsAPI(), report);
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
			if (Strings.isNullOrEmpty(report.getProbeId())) {
				report.setProbeId(ProbeConfiguration.probeId);
			}
			report.setSent(true);
			log.info("Sending network report to the server with id: " + report.getId());
			RESTUtils.sendPost(ProbeConfiguration.getInstance().getLoadedConfigItems().getReportsAPI() + "/network", report);
			DBUtil.getInstance().merge(report);
		}

	}

	/**
	 * This function retrieves all {@link Report} objects from the database where sent tag is false.
	 */
	private void sendReportsToServer() {
		List<Report> reports = DBUtil.getInstance().findByFieldName("isSent", false, new Report());
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
		List<NetworkReport> networkReports = DBUtil.getInstance().findByFieldName("sent", false, new NetworkReport());
		if (networkReports != null) {
			for (NetworkReport networkReport : networkReports) {
				sendNetworkReport(networkReport);
			}
		}
	}

}