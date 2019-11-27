package com.simple2secure.api.model.validation;

public class ValidInputTestRun extends ValidatedInput<String>{
	
	private String testRunId;
	private String tag = "/{testRunId}";
	
	public ValidInputTestRun() {
	}
	
	public ValidInputTestRun(String testRunId) {
		this.testRunId = testRunId;
	}

	@Override
	public String getValue() {
		return testRunId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
