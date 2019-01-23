package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Notification extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 3081134740537811621L;
	private String contextId;
	private String toolId;
	private String name;
	private String content;
	private String timestamp;
	private boolean read;

	public Notification() {
	}

	public Notification(String contextId, String toolId, String name, String content, String timestamp, boolean read) {
		super();
		this.contextId = contextId;
		this.toolId = toolId;
		this.name = name;
		this.content = content;
		this.timestamp = timestamp;
		this.read = read;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
