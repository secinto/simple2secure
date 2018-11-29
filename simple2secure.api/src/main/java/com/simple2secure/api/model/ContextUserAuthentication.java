package com.simple2secure.api.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class ContextUserAuthentication {

	private String userId;
	private String contextId;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

}
