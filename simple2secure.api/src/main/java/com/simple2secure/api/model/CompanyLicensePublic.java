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
import javax.persistence.Lob;
import javax.persistence.Table;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(
		ignoreUnknown = true)
@Entity
@Table(
		name = "CompanyLicenseObj")
@Getter
@Setter
@NoArgsConstructor
public class CompanyLicensePublic extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7011645066859754490L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	protected ObjectId groupId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	protected ObjectId deviceId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	protected ObjectId licenseId;

	@Lob
	protected String accessToken;

	@Lob
	protected String refreshToken;

	protected String expirationDate;

	@JsonProperty
	protected boolean activated;

	public CompanyLicensePublic(ObjectId groupId, ObjectId licenseId) {
		setGroupId(groupId);
		setLicenseId(licenseId);
	}

	public CompanyLicensePublic(ObjectId groupId, ObjectId licenseId, String expirationDate) {
		this(groupId, licenseId);
		setExpirationDate(expirationDate);
	}

	public CompanyLicensePublic(ObjectId groupId, ObjectId licenseId, String expirationDate, ObjectId deviceId) {
		this(groupId, licenseId, expirationDate);
		setDeviceId(deviceId);
	}

	public CompanyLicensePublic(ObjectId groupId, ObjectId licenseId, String expirationDate, ObjectId deviceId, String accessToken) {
		this(groupId, licenseId, expirationDate, deviceId);
		setAccessToken(accessToken);
	}

	public CompanyLicensePublic(ObjectId groupId, ObjectId licenseId, String expirationDate, ObjectId deviceId, String accessToken,
			String refreshToken) {
		this(groupId, licenseId, expirationDate, deviceId, accessToken);
		setRefreshToken(refreshToken);
	}
}
