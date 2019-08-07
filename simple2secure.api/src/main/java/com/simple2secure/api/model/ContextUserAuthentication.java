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
