package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputRuleFactType extends ValidatedInput<String> {

	private String ruleFactType;
	private String tag = "/{ruleFactType}";

	public ValidInputRuleFactType() {
	}

	public ValidInputRuleFactType(String ruleFactType) {
		this.ruleFactType = ruleFactType;
	}

	@Override
	public String getValue() {
		return ruleFactType;
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
		return new ValidInputRuleFactType(value);
	}

}
