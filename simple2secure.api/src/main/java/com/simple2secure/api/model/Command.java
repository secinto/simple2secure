package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Command extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6467606054044990890L;

	private String content;

	public Command() {

	}

	public Command(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
