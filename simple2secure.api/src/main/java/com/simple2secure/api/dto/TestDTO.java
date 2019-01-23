package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.TestCase;
import com.simple2secure.api.model.TestCaseResult;

public class TestDTO {

	private TestCase test;

	private List<TestCaseResult> results;

	public TestDTO(TestCase test) {
		this.test = test;
	}

	public TestDTO(TestCase test, List<TestCaseResult> results) {
		this.test = test;
		this.results = results;
	}

	public TestCase getTest() {
		return test;
	}

	public void setTest(TestCase test) {
		this.test = test;
	}

	public List<TestCaseResult> getResults() {
		return results;
	}

	public void setResults(List<TestCaseResult> results) {
		this.results = results;
	}

	public void addResult(TestCaseResult result) {
		results.add(result);
	}
}
