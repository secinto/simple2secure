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

public class LicensePlan extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 3676662699255701433L;

	private String name;
	private long validity;
	private TimeUnit validityUnit;
	private int maxNumberOfDownloads;

	public LicensePlan() {

	}

	public LicensePlan(String name, long validity, TimeUnit validityUnit, int maxNumberOfDownloads) {
		this.name = name;
		this.validity = validity;
		this.validityUnit = validityUnit;
		this.maxNumberOfDownloads = maxNumberOfDownloads;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long validity) {
		this.validity = validity;
	}

	public TimeUnit getValidityUnit() {
		return validityUnit;
	}

	public void setValidityUnit(TimeUnit validityUnit) {
		this.validityUnit = validityUnit;
	}

	public int getMaxNumberOfDownloads() {
		return maxNumberOfDownloads;
	}

	public void setMaxNumberOfDownloads(int maxNumberOfDownloads) {
		this.maxNumberOfDownloads = maxNumberOfDownloads;
	}
}
