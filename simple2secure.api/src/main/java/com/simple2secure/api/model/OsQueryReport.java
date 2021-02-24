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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "OsQueryReport")
@Getter
@Setter
@NoArgsConstructor
public class OsQueryReport extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7217360147886001090L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId queryId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId deviceId;
	@Lob
	private String query;
	private String name;
	private String hostname;

	@Lob
	private String queryResult;

	private Date queryTimestamp;
	private boolean isSent;

	/**
	 *
	 * @param name
	 * @param report_class
	 * @param interval
	 */
	public OsQueryReport(ObjectId deviceId, String query, String queryResult, Date queryTimestamp, boolean isSent) {
		super();
		this.deviceId = deviceId;
		this.query = query;
		this.queryResult = queryResult;
		this.queryTimestamp = queryTimestamp;
		this.isSent = isSent;
	}
}
