package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputWidget extends ValidatedInput<String> {

	private String widgetId;
	private String tag = "/{widgetId}";

	public ValidInputWidget() {
	}

	public ValidInputWidget(String widgetId) {
		this.widgetId = widgetId;
	}

	@Override
	public String getValue() {
		return widgetId;
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
		return new ValidInputWidget(value);
	}
}
