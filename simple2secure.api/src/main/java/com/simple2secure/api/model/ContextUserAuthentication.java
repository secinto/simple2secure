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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.simple2secure.api.dbo.GenericDBObject;

public class ContextUserAuthentication extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -1144793004982781563L;
	private String userId;
	private String contextId;
	private boolean ownContext;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	public ContextUserAuthentication() {
	}

	public ContextUserAuthentication(String userId, String contextId, UserRole userRole, boolean ownContext) {
		this.userId = userId;
		this.contextId = contextId;
		this.userRole = userRole;
		this.ownContext = ownContext;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public boolean isOwnContext() {
		return ownContext;
	}

	public void setOwnContext(boolean ownContext) {
		this.ownContext = ownContext;
	}
}
