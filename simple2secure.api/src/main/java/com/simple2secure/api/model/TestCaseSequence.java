package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestCaseSequence extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -8816207282621092077L;

	private String toolId;

	private List<TestCase> testCases = new ArrayList<>();

	private boolean finished = false;

	private boolean scheduled = false;

	public TestCaseSequence() {

	}

	public TestCaseSequence(String toolId, List<TestCase> testCases) {
		this.testCases = testCases;
		this.toolId = toolId;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	public void addTestCase(TestCase testCase) {
		testCases.add(testCase);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
}
