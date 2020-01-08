package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputWidgetLocation extends ValidatedInput<String> {

	private String widgetLocation;
	private String tag = "/{widgetLocation}";

	public ValidInputWidgetLocation() {
	}

	public ValidInputWidgetLocation(String widgetLocation) {
		this.widgetLocation = widgetLocation;
	}

	@Override
	public String getValue() {
		return widgetLocation;
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
		return new ValidInputWidgetLocation(value);
	}
}
