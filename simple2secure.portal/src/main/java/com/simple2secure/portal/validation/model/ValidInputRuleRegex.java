package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputRuleRegex extends ValidatedInput<String> {

	private ObjectId ruleRegexId;
	private String tag = "/{ruleRegexId}";

	public ValidInputRuleRegex() {
	}

	public ValidInputRuleRegex(String ruleRegexId) {
		this.ruleRegexId = new ObjectId(ruleRegexId);
	}

	@Override
	public ObjectId getValue() {
		return ruleRegexId;
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
		return new ValidInputRuleRegex(value);
	}
}
