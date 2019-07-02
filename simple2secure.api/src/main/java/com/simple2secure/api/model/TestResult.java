package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResult extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6306698374548193553L;

	private String name;
	private String testId;
	private String hostname;
	private String result;
	private String timestamp;

	public TestResult() {

	}

	public TestResult(String name, String testId, String hostname, String result, String timestamp) {
		this.name = name;
		this.testId = testId;
		this.hostname = hostname;
		this.result = result;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
