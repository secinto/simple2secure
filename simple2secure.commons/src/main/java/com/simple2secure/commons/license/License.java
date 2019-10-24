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
package com.simple2secure.commons.license;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.simple2secure.commons.time.TimeUtils;

public class License {
	public static final String EXPIRATION_DATE = "expirationDate";
	private Properties licenseProperties;

	public License(Properties licenseProperties) {
		this.licenseProperties = licenseProperties;
	}

	public Date getExpirationDate() {
		return TimeUtils.parseDate(TimeUtils.SIMPLE_DATE_FORMAT, getProperty(EXPIRATION_DATE));
	}

	public String getExpirationDateAsString() {
		return getProperty(EXPIRATION_DATE);
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > getExpirationDate().getTime();
	}

	public Properties getProperties() {
		return licenseProperties;
	}

	public String getProperty(String name) {
		return licenseProperties.getProperty(name);
	}

	public List<String> getPropertyNames() {
		List<String> propertyNames = new ArrayList<>();
		Enumeration<?> keys = licenseProperties.propertyNames();
		while (keys.hasMoreElements()) {
			propertyNames.add((String) keys.nextElement());
		}

		return propertyNames;
	}

	@Override
	public String toString() {
		return licenseProperties.toString();
	}

}
