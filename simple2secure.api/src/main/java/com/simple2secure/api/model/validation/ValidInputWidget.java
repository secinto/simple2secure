package com.simple2secure.api.model.validation;

public class ValidInputWidget extends ValidatedInput<String>{
	
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
}
