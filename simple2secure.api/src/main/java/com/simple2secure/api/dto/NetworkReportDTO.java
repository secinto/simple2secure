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
package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.simple2secure.api.model.Coordinates;
import com.simple2secure.api.model.NetworkReport;

public class NetworkReportDTO extends NetworkReport {
	/**
	 *
	 */
	private static final long serialVersionUID = 2233458870207070618L;

	private List<Coordinates> coordinates = new ArrayList<>();

	public NetworkReportDTO() {

	}

	public NetworkReportDTO(List<Coordinates> coordinates, String reportId, String groupId, Date startTime, String processorName) {
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
