package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.UserInfo;
import com.simple2secure.api.model.UserRole;

public class UserRoleDTO {

	private UserInfo user;
	private UserRole userRole;
	private List<String> groupIds;

	public UserRoleDTO() {

	}

	public UserRoleDTO(UserInfo user, UserRole userRole) {
		this.user = user;
		this.userRole = userRole;
	}

	public UserRoleDTO(UserInfo user, UserRole userRole, List<String> groupIds) {
		this.user = user;
		this.userRole = userRole;
		this.groupIds = groupIds;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
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
}
