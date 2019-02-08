package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResultTestMapping extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6306698374548193553L;

	private String testResultId;
	private String testId;

	public TestResultTestMapping() {

	}

	public TestResultTestMapping(String testResultId, String testId) {
		this.testResultId = testResultId;
		this.testId = testId;
	}

	public String getTestResultId() {
		return testResultId;
	}

	public void setTestResultId(String testResultId) {
		this.testResultId = testResultId;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

}
