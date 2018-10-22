/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import com.simple2secure.api.model.Report;

public class ReportDTO extends Report {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4813182193819532383L;
	private String username;

	/**
	 *
	 * @param name
	 * @param report_class
	 * @param interval
	 */
	public ReportDTO(String username, Report report) {
		super(report.getProbeId(), report.getQuery(), report.getQueryResult(), report.getQueryTimestamp(), true);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
