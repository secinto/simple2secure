package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.simple2secure.api.dbo.GenericDBObject;

public class UserInvitation extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 2197896605268219184L;

	private String userId;

	private String contextId;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	private String invitationToken;

	private long invitationTokenExpirationTime;

	List<String> groupIds = new ArrayList<>();

	public UserInvitation() {

	}

	public UserInvitation(String userId, String contextId, UserRole userRole, String invitationToken, long invitationTokenExpirationTime,
			List<String> groupIds) {
		this.userId = userId;
		this.contextId = contextId;
		this.userRole = userRole;
		this.invitationToken = invitationToken;
		this.invitationTokenExpirationTime = invitationTokenExpirationTime;
		this.groupIds = groupIds;
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

	public String getInvitationToken() {
		return invitationToken;
	}

	public void setInvitationToken(String invitationToken) {
		this.invitationToken = invitationToken;
	}

	public long getInvitationTokenExpirationTime() {
		return invitationTokenExpirationTime;
	}

	public void setInvitationTokenExpirationTime(long invitationTokenExpirationTime) {
		this.invitationTokenExpirationTime = invitationTokenExpirationTime;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}
}
