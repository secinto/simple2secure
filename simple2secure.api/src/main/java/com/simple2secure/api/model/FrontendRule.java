package com.simple2secure.api.model;

public class FrontendRule {
	
	private String id;
	private String toolId;
	private String contextId;
	private String name;
	private String description;
	private int priority;
	private String createdOn;
	private boolean active;
	
	public FrontendRule() {
	}

	public FrontendRule(String id, String toolId, String contextId, String name, String description, int priority, String createdOn, boolean active) {
		this.id = id;
		this.toolId = toolId;
		this.contextId = contextId;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.createdOn = createdOn;
		this.active = active;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
