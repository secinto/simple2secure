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

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "QueryConfig")
public class QueryConfig extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6832309845224642179L;
	private String userUUID;
	private String querystrings;
	private String queryconfig;
	private String osquerypath;

	public QueryConfig() {

	}

	public QueryConfig(String userUUID, String querystrings, String queryconfig, String osquerypath) {
		super();
		this.userUUID = userUUID;
		this.querystrings = querystrings;
		this.queryconfig = queryconfig;
		this.osquerypath = osquerypath;
	}

	public String getQuerystrings() {
		return querystrings;
	}

	public void setQuerystrings(String querystrings) {
		this.querystrings = querystrings;
	}

	public String getQueryconfig() {
		return queryconfig;
	}

	public void setQueryconfig(String queryconfig) {
		this.queryconfig = queryconfig;
	}

	public String getOsquerypath() {
		return osquerypath;
	}

	public void setOsquerypath(String osquerypath) {
		this.osquerypath = osquerypath;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

}
