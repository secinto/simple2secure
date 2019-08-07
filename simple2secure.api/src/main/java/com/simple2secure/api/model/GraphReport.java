package com.simple2secure.api.model;

public class GraphReport {

	private String reportId;
	private String reportName;
	private int numberOfReports;
	private long timestamp;

	public GraphReport() {

	}

	public GraphReport(String reportId, String reportName, int numberOfReports, long timestamp) {
		this.reportId = reportId;
		this.reportName = reportName;
		this.numberOfReports = numberOfReports;
		this.timestamp = timestamp;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public int getNumberOfReports() {
		return numberOfReports;
	}

	public void setNumberOfReports(int numberOfReports) {
		this.numberOfReports = numberOfReports;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
