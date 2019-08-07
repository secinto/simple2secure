package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class PortalRule extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6811188688302072478L;
	
	private String toolId;
	private String userId;
	private ExtendedRule rule;
	private String createdOn;
	private boolean active;

	public PortalRule() {
	}

	public PortalRule(String toolId, String userId, ExtendedRule rule, String createdOn, boolean active) {
		this.toolId = toolId;
		this.userId = userId;
		this.rule = rule;
		this.createdOn = createdOn;
		this.active = active;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ExtendedRule getRule() {
		return rule;
	}

	public void setRule(ExtendedRule rule) {
		this.rule = rule;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
