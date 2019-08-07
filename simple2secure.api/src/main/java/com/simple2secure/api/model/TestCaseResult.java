package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestCaseResult extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 8378078482721033979L;
	private String name;
	private String content;
	private long timestamp;

	public TestCaseResult() {
	}

	public TestCaseResult(String name, String content, long timestamp) {
		this.name = name;
		this.content = content;
		this.timestamp = timestamp;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
