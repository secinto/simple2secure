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

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class UserRegistration {

	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	private List<String> groupIds;

	private String addedByUserId;

	private String currentContextId;

	private UserRegistrationType registrationType;

	public UserRegistration() {

	}

	public UserRegistration(String email, UserRole userRole, List<String> groupIds) {
		this.email = email;
		this.userRole = userRole;
		this.groupIds = groupIds;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	public String getAddedByUserId() {
		return addedByUserId;
	}

	public String getCurrentContextId() {
		return currentContextId;
	}

	public void setCurrentContextId(String currentContextId) {
		this.currentContextId = currentContextId;
	}

	public void setAddedByUserId(String addedByUserId) {
		this.addedByUserId = addedByUserId;
	}

	public UserRegistrationType getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(UserRegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
