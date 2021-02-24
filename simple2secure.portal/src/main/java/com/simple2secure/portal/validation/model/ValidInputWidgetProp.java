package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputWidgetProp extends ValidatedInput<String> {

	private ObjectId widgetPropId;
	private String tag = "/{widgetPropId}";

	public ValidInputWidgetProp() {
	}

	public ValidInputWidgetProp(String widgetPropId) {
		this.widgetPropId = new ObjectId(widgetPropId);
	}

	@Override
	public ObjectId getValue() {
		return widgetPropId;
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
		return new ValidInputWidgetProp(value);
	}
}
