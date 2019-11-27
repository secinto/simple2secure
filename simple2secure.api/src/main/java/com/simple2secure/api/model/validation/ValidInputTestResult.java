package com.simple2secure.api.model.validation;

public class ValidInputTestResult extends ValidatedInput<String>{
	
	private String testResultId;
	private String tag = "/{testResultId}";
	
	public ValidInputTestResult() {
	}
	
	public ValidInputTestResult(String testResultId) {
		this.testResultId = testResultId;
	}

	@Override
	public String getValue() {
		return testResultId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
