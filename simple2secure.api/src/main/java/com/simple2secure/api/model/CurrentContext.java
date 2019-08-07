package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class CurrentContext extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -1653335597890726900L;

	private String userId;

	private String contextUserAuthenticationId;

	public CurrentContext() {
	}

	public CurrentContext(String userId, String contextUserAuthenticationId) {
		this.userId = userId;
		this.contextUserAuthenticationId = contextUserAuthenticationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContextUserAuthenticationId() {
		return contextUserAuthenticationId;
	}

	public void setContextUserAuthenticationId(String contextUserAuthenticationId) {
		this.contextUserAuthenticationId = contextUserAuthenticationId;
	}

}
