package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputDestGroup extends ValidatedInput<String> {

	private String destGroupId;
	private String tag = "/{destGroupId}";

	public ValidInputDestGroup() {
	}

	public ValidInputDestGroup(String destGroupId) {
		this.destGroupId = destGroupId;
	}

	@Override
	public String getValue() {
		return destGroupId;
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
		return new ValidInputDestGroup(value);
	}
}
