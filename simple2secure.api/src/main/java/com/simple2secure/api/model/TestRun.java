package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestRun extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 8963088362714211548L;
	private String testId;
	private String podId;
	private TestRunType testRunType;
	private boolean executed;

	public TestRun() {

	}

	public TestRun(String testId, String podId, boolean executed, TestRunType testRunType) {
		this.testId = testId;
		this.podId = podId;
		this.executed = executed;
		this.testRunType = testRunType;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
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
}
