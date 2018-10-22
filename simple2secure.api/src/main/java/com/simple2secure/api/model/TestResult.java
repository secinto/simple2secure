package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResult extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8378078482721033979L;
	private String name;
	private String content;
	private String timestamp;

	public TestResult() {
	}

	public TestResult(String name, String content, String timestamp) {
		this.name = name;
		this.content = content;
		this.timestamp = timestamp;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
