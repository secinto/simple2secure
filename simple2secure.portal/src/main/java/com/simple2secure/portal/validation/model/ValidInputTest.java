package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTest extends ValidatedInput<String> {

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

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputTest(value);
	}
}
