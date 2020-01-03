package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputReport extends ValidatedInput<String> {

	private String reportId;
	private String tag = "/{reportId}";

	public ValidInputReport() {
	}

	public ValidInputReport(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String getValue() {
		return reportId;
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
		return new ValidInputReport(value);
	}
}
