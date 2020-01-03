/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.OsQueryReport;

public class OsQueryReportDTO {

	private long totalSize;
	private List<OsQueryReport> report;

	public OsQueryReportDTO() {

	}

	/**
	 *
	 * @param name
	 * @param report_class
	 * @param interval
	 */
	public OsQueryReportDTO(List<OsQueryReport> report, long totalSize) {
		this.report = report;
		this.totalSize = totalSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public List<OsQueryReport> getReport() {
		return report;
	}

	public void setReport(List<OsQueryReport> report) {
		this.report = report;
	}

}
