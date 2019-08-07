package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class FrontendRule extends GenericDBObject {

	/**
	 *
	 */

	private static final long serialVersionUID = 3173461203987223971L;
	private String toolId;
	private String contextId;
	private String clazz;
	private String name;
	private String description;
	private int priority;
	private long timestamp;
	private boolean active = true;

	public FrontendRule() {
	}

	public FrontendRule(String toolId, String contextId, String clazz, String name, String description, int priority, long timestamp,
			boolean active) {
		this.toolId = toolId;
		this.contextId = contextId;
		this.clazz = clazz;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.timestamp = timestamp;
		this.active = active;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
