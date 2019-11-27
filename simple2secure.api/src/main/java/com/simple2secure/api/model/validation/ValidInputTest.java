package com.simple2secure.api.model.validation;

public class ValidInputTest extends ValidatedInput<String>{
	
	private String testId;
	private String tag = "/{testId}";
	
	public ValidInputTest() {
	}
	
	public ValidInputTest(String testId) {
		this.testId = testId;
	}

	@Override
	public String getValue() {
		return testId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
