/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
package com.simple2secure.commons.license;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class License {
	public static final String EXPIRATION_DATE = "expirationDate";
	public static final String LICENSE_DATE_FORMAT = "MM/dd/yyyy";

	private Properties licenseProperties;

	public License(Properties licenseProperties) {
		this.licenseProperties = licenseProperties;
	}

	public Date getExpirationDate() {
		try {
			return new SimpleDateFormat(LICENSE_DATE_FORMAT).parse(getProperty(EXPIRATION_DATE));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
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
