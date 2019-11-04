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

import java.util.List;

import com.simple2secure.api.model.NetworkReport;

public class NetworkReportDTO {

	private List<NetworkReport> report;

	private long totalSize;

	public NetworkReportDTO() {

	}

	public NetworkReportDTO(List<NetworkReport> report, long totalSize) {
		super();
		this.report = report;
		this.totalSize = totalSize;
	}

	public List<NetworkReport> getReport() {
		return report;
	}

	public void setReports(List<NetworkReport> report) {
		this.report = report;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
}
