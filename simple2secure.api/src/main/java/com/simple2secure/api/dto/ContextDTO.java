package com.simple2secure.api.dto;

import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.UserRole;

public class ContextDTO {
	private Context context;
	private UserRole userRole;

	public ContextDTO() {
	}

	public ContextDTO(Context context, UserRole userRole) {
		this.context = context;
		this.userRole = userRole;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
