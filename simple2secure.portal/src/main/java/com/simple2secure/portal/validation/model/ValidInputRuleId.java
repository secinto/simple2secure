package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputRuleId extends ValidatedInput<String> {

	private ObjectId ruleId;
	private String tag = "/{ruleId}";

	public ValidInputRuleId() {
	}

	public ValidInputRuleId(String ruleId) {
		this.ruleId = new ObjectId(ruleId);
	}

	@Override
	public ObjectId getValue() {
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
		return new ValidInputRuleId(value);
	}
}
