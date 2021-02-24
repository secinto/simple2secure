package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTestResult extends ValidatedInput<String> {

	private ObjectId testResultId;
	private String tag = "/{testResultId}";

	public ValidInputTestResult() {
	}

	public ValidInputTestResult(String testResultId) {
		this.testResultId = new ObjectId(testResultId);
	}

	@Override
	public ObjectId getValue() {
		return testResultId;
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
		return new ValidInputTestResult(value);
	}
}
