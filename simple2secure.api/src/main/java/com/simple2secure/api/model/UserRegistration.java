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
