package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResult extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6306698374548193553L;

	private String name;
	private String testRunId;
	private String hostname;
	private String result;
	private String timestamp;

	public TestResult() {

	}

	public TestResult(String name, String testRunId, String hostname, String result, String timestamp) {
		this.name = name;
		this.testRunId = testRunId;
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

	public String getTestRunId() {
		return testRunId;
	}

	public void setTestRunId(String testRunId) {
		this.testRunId = testRunId;
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
