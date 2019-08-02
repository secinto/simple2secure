package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestRun extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 8963088362714211548L;
	private String testId;
	private String testName;
	private String podId;
	private String contextId;
	private TestRunType testRunType;
	private boolean executed;
	private String testContent;
	private TestStatus testStatus;
	private long timestamp;

	public TestRun() {

	}

	public TestRun(String testId, String testName, String podId, String contextId, boolean executed, TestRunType testRunType,
			String testContent, TestStatus testStatus, long timestamp) {
		this.testId = testId;
		this.testName = testName;
		this.podId = podId;
		this.contextId = contextId;
		this.executed = executed;
		this.testRunType = testRunType;
		this.testContent = testContent;
		this.testStatus = testStatus;
		this.timestamp = timestamp;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public TestRunType getTestRunType() {
		return testRunType;
	}

	public void setTestRunType(TestRunType testRunType) {
		this.testRunType = testRunType;
	}

	public String getTestContent() {
		return testContent;
	}

	public void setTestContent(String testContent) {
		this.testContent = testContent;
	}

	public TestStatus getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(TestStatus testStatus) {
		this.testStatus = testStatus;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
