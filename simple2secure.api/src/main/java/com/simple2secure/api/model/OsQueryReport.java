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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "OsQueryReport")
public class OsQueryReport extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7217360147886001090L;
	private String queryId;
	private String deviceId;
	@Lob
	private String query;
	private String name;
	private String hostname;

	@Lob
	private String queryResult;

	private Date queryTimestamp;
	private boolean isSent;

	public OsQueryReport() {

	}

	/**
	 *
	 * @param name
	 * @param report_class
	 * @param interval
	 */
	public OsQueryReport(String deviceId, String query, String queryResult, Date queryTimestamp, boolean isSent) {
		super();
		this.deviceId = deviceId;
		this.query = query;
		this.queryResult = queryResult;
		this.queryTimestamp = queryTimestamp;
		this.isSent = isSent;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(String queryResult) {
		this.queryResult = queryResult;
	}

	public Date getQueryTimestamp() {
		return queryTimestamp;
	}

	public void setQueryTimestamp(Date queryTimestamp) {
		this.queryTimestamp = queryTimestamp;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
