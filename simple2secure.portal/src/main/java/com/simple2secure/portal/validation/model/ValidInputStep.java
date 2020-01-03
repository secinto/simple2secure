package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputStep extends ValidatedInput<String> {

	private String stepId;
	private String tag = "/{stepId}";

	public ValidInputStep() {
	}

	public ValidInputStep(String stepId) {
		this.stepId = stepId;
	}

	@Override
	public String getValue() {
		return stepId;
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
		return new ValidInputStep(value);
	}
}
