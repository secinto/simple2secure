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

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(
		name = "QueryRun")
public class QueryRun extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 4400048729580737036L;
	private String groupId;
	private String name;

	@JsonProperty
	private boolean always;
	private int analysisInterval;
	private TimeUnit analysisIntervalUnit;
	@JsonProperty
	private boolean osquery;
	private String sqlQuery;
	private int active;
	private int systemsAvailable;

	@JsonProperty
	private boolean graphAble;

	public static int NONE = 0;
	public static int WINDOWS = 1;
	public static int LINUX = 2;
	public static int MACOS = 4;

	public QueryRun() {
		systemsAvailable = NONE;
		graphAble = true;
		osquery = true;
	}

	public QueryRun(String groupId, String name, boolean always, int analysisInterval, TimeUnit analysisIntervalUnit, String sqlQuery,
			int active) {
		super();
		this.groupId = groupId;
		this.name = name;
		this.always = always;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.sqlQuery = sqlQuery;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAlways() {
		return always;
	}

	public void setAlways(boolean always) {
		this.always = always;
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

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getSystemsAvailable() {
		return systemsAvailable;
	}

	public void setSystemsAvailable(int systemsAvailable) {
		this.systemsAvailable = systemsAvailable;
	}

	public boolean isGraphAble() {
		return graphAble;
	}

	public void setGraphAble(boolean graphAble) {
		this.graphAble = graphAble;
	}

}
