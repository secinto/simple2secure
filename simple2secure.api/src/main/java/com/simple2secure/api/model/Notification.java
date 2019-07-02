package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Notification extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 3081134740537811621L;
	private String content;
	private String contextId;
	private boolean read;

	public Notification() {
	}

	public Notification(String content, boolean read, String contextId) {
		super();
		this.content = content;
		this.contextId = contextId;
		this.read = read;
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
}
