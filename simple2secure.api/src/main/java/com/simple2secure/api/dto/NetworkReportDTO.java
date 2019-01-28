package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.model.Coordinates;
import com.simple2secure.api.model.NetworkReport;

public class NetworkReportDTO extends NetworkReport {
	/**
	 *
	 */
	private static final long serialVersionUID = 2233458870207070618L;

	private List<Coordinates> coordinates = new ArrayList<Coordinates>();

	public NetworkReportDTO() {

	}

	public NetworkReportDTO(List<Coordinates> coordinates, String reportId, String groupId, String startTime, String processorName) {
		super.setId(reportId);
		super.setGroupId(groupId);
		super.setStartTime(startTime);
		super.setProcessorName(processorName);
		this.coordinates = coordinates;
	}

	public List<Coordinates> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinates> coordinates) {
		this.coordinates = coordinates;
	}

}
