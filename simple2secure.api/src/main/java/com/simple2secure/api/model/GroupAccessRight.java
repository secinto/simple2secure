package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class GroupAccessRight extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -947703623211244169L;
	String userId;
	String groupId;
	String contextId;

	public GroupAccessRight() {

	}

	public GroupAccessRight(String userId, String groupId, String contextId) {
		this.userId = userId;
		this.groupId = groupId;
		this.contextId = contextId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
}
