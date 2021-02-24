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
import javax.persistence.Lob;
import javax.persistence.Table;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
		name = "OsQuery")
public class OsQuery extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 4400048729580737036L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId categoryId;

	private String name;
	private String description;

	private int analysisInterval;
	private TimeUnit analysisIntervalUnit;
	@JsonProperty
	private boolean osquery;
	@Lob
	private String sqlQuery;
	private int active;
	private int systemsAvailable;

	@JsonProperty
	private boolean graphAble;

	@JsonProperty
	private boolean availabilityCheck;

	@JsonProperty
	private boolean fixedSize;

	public static int NONE = 0;
	public static int WINDOWS = 1;
	public static int LINUX = 2;
	public static int MACOS = 4;

	public OsQuery() {
		systemsAvailable = NONE;
		graphAble = true;
		osquery = true;
	}

	public OsQuery(String name, int analysisInterval, TimeUnit analysisIntervalUnit, String sqlQuery, int active) {
		super();
		this.name = name;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.sqlQuery = sqlQuery;
		this.active = active;
	}
}
