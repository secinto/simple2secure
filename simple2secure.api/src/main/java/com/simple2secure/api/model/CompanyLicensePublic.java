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
@Table(
		name = "CompanyLicenseObj")
public class CompanyLicensePublic extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7011645066859754490L;

	protected String groupId;

	protected String deviceId;

	protected String licenseId;

	protected String accessToken;

	protected String expirationDate;

	protected boolean activated = false;

	protected boolean deviceIsPod = false;

	protected String hostname;

	protected PodStatus status = PodStatus.UNKNOWN;

	protected long lastOnlineTimestamp;

	public CompanyLicensePublic() {
	}

	public CompanyLicensePublic(String groupId, String licenseId) {
		this.groupId = groupId;
		this.licenseId = licenseId;
	}

	public CompanyLicensePublic(String groupId, String licenseId, String expirationDate) {
		this(groupId, licenseId);
		this.expirationDate = expirationDate;
	}

	public CompanyLicensePublic(String groupId, String licenseId, String expirationDate, String deviceId) {
		this(groupId, licenseId, expirationDate);
		this.deviceId = deviceId;
	}

	public CompanyLicensePublic(String groupId, String licenseId, String expirationDate, String deviceId, String hostname) {
		this(groupId, licenseId, expirationDate, deviceId);
		this.hostname = hostname;
	}

	public CompanyLicensePublic(String groupId, String licenseId, String expirationDate, String deviceId, String hostname,
			String accessToken) {
		this(groupId, licenseId, expirationDate, deviceId, hostname);
		this.accessToken = accessToken;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean isDevicePod() {
		return deviceIsPod;
	}

	public void setDevicePod(boolean deviceIsPod) {
		this.deviceIsPod = deviceIsPod;
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public PodStatus getStatus() {
		return status;
	}

	public void setStatus(PodStatus status) {
		this.status = status;
	}

	public long getLastOnlineTimestamp() {
		return lastOnlineTimestamp;
	}

	public void setLastOnlineTimestamp(long lastOnlineTimestamp) {
		this.lastOnlineTimestamp = lastOnlineTimestamp;
	}
}
