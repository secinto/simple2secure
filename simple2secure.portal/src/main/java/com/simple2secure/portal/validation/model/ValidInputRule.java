package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputRule extends ValidatedInput<String> {

	private String ruleId;
	private String tag = "/{ruleId}";

	public ValidInputRule() {
	}

	public ValidInputRule(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String getValue() {
		return ruleId;
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
		return new ValidInputRule(value);
	}
}
