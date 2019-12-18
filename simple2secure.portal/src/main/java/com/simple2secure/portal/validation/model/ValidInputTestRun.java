package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTestRun extends ValidatedInput<String> {

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

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputTestRun(value);
	}
}
