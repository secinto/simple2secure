package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputEmailConfig extends ValidatedInput<String> {

	private String emailConfigId;
	private String tag = "/{emailConfigId}";

	public ValidInputEmailConfig() {
	}

	public ValidInputEmailConfig(String emailConfigId) {
		this.emailConfigId = emailConfigId;
	}

	@Override
	public String getValue() {
		return emailConfigId;
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
		return new ValidInputEmailConfig(value);
	}
}
