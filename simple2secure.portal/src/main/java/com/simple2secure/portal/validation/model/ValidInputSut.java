package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputSut extends ValidatedInput<String> {

	private String sutId;
	private String tag = "/{sutId}";

	public ValidInputSut() {
	}

	public ValidInputSut(String version) {
		sutId = version;
	}

	@Override
	public String getValue() {
		return sutId;
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
		return new ValidInputSut(value);
	}
}
