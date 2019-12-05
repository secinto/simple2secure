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
package com.simple2secure.api.model;
import java.util.concurrent.TimeUnit;

import com.simple2secure.api.dbo.GenericDBObject;

public class QueryGroupMapping extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 4400048729580737036L;
	private String groupId;
	private String queryId;
	private int analysisInterval;
	private TimeUnit analysisIntervalUnit;
	private int systemsAvailable;

	public QueryGroupMapping() {
	}

	public QueryGroupMapping(String groupId, String queryId, int analysisInterval, TimeUnit analysisIntervalUnit, int systemsAvailable) {
		this.groupId = groupId;
		this.queryId = queryId;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.systemsAvailable = systemsAvailable;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public int getAnalysisInterval() {
		return analysisInterval;
	}

	public void setAnalysisInterval(int analysisInterval) {
		this.analysisInterval = analysisInterval;
	}

	public TimeUnit getAnalysisIntervalUnit() {
		return analysisIntervalUnit;
	}

	public void setAnalysisIntervalUnit(TimeUnit analysisIntervalUnit) {
		this.analysisIntervalUnit = analysisIntervalUnit;
	}

	public int getSystemsAvailable() {
		return systemsAvailable;
	}

	public void setSystemsAvailable(int systemsAvailable) {
		this.systemsAvailable = systemsAvailable;
	}
}
