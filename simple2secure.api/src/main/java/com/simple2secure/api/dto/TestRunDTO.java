package com.simple2secure.api.dto;

import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;

public class TestRunDTO {

	private TestRun testRun;

	private TestResult testResult;

	public TestRunDTO() {

	}

	public TestRunDTO(TestRun testRun, TestResult testResult) {
		this.testResult = testResult;
		this.testRun = testRun;
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public void setTestRun(TestRun testRun) {
		this.testRun = testRun;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
