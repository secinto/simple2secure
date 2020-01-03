package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputOsinfo extends ValidatedInput<String> {

	private String osinfo;
	private String tag = "/{osinfo}";

	public ValidInputOsinfo() {
	}

	public ValidInputOsinfo(String osinfo) {
		this.osinfo = osinfo;
	}

	@Override
	public String getValue() {
		return osinfo;
	}

	public void setValue(String value) {
		osinfo = value;
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
		return new ValidInputOsinfo(value);
	}
}
