package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.simple2secure.api.model.SystemType;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputSystemType extends ValidatedInput<String> {

	private String systemType;
	private String tag = "/{systemType}";

	public ValidInputSystemType() {
	}

	public ValidInputSystemType(String systemType) {
		this.systemType = systemType;
	}

	@Override
	public String getValue() {
		return systemType;
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

		String systemType = SystemType.getSystemType(value).toString();

		return new ValidInputSystemType(systemType);
	}
}
