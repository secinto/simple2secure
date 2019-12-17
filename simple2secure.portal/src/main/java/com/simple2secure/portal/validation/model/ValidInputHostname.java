package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputHostname extends ValidatedInput<String> {

	private String hostname;
	private String tag = "/{hostname}";

	public ValidInputHostname() {
	}

	public ValidInputHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getValue() {
		return hostname;
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
		return new ValidInputHostname(value);
	}
}
