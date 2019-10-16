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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	private String deviceId;
	private CompanyGroup group;
	@JsonProperty
	private boolean activated;
	private String hostname;
	private String status;
	@JsonProperty
	private boolean pod;

	public Device() {

	}

	public Device(String deviceId, CompanyGroup group, boolean activated, String hostname, String status, boolean pod) {
		super();

		this.deviceId = deviceId;
		this.group = group;
		this.activated = activated;
		this.hostname = hostname;
		this.status = status;
		this.pod = pod;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public CompanyGroup getGroup() {
		return group;
	}

	public void setGroup(CompanyGroup group) {
		this.group = group;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isPod() {
		return pod;
	}

	public void setPod(boolean pod) {
		this.pod = pod;
	}

}
