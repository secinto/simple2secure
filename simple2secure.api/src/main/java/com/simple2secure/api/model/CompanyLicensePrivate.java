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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "License")
@Getter
@Setter
@NoArgsConstructor
public class CompanyLicensePrivate extends CompanyLicensePublic {

	/**
	 *
	 */
	private static final long serialVersionUID = 8652284780091080199L;

	private String tokenSecret;

	private long lastTokenRefresh = 0;

	public CompanyLicensePrivate(String groupId, String licenseId, String expirationDate) {
		super(groupId, licenseId, expirationDate);
	}

	/**
	 * Function returns the {@link CompanyLicensePublic} object for this {@link CompanyLicensePrivate} object. It cleans all sensitive
	 * information such that serializing this object doesn't leak any information.
	 *
	 * @return
	 */
	public CompanyLicensePublic getPublicLicense() {
		CompanyLicensePublic publicLicense = new CompanyLicensePublic(groupId, licenseId, expirationDate, deviceId);
		publicLicense.setAccessToken(accessToken);
		return publicLicense;
	}

	public CompanyLicensePrivate copyLicense() {
		CompanyLicensePrivate license = new CompanyLicensePrivate(groupId, licenseId, expirationDate);
		return license;
	}
}
