package com.simple2secure.api.dto;

import com.simple2secure.api.model.NetworkReport;

public class NetworkReportDTO extends NetworkReport {
	/**
	 *
	 */
	private static final long serialVersionUID = 2233458870207070618L;
	private Double latitude;
	private Double longitude;

	public NetworkReportDTO() {

	}

	public NetworkReportDTO(Double latitude, Double longitude, String reportId, String groupId, String startTime, String processorName) {
		super.setId(reportId);
		super.setGroupId(groupId);
		super.setStartTime(startTime);
		super.setProcessorName(processorName);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
