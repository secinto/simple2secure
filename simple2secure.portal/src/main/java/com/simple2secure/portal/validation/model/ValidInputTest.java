package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTest extends ValidatedInput<String> {

	private ObjectId testId;
	private String tag = "/{testId}";

	public ValidInputTest() {
	}

	public ValidInputTest(String testId) {
		this.testId = new ObjectId(testId);
	}

	@Override
	public ObjectId getValue() {
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
