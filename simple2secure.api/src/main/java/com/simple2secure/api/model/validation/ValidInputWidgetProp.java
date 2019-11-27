package com.simple2secure.api.model.validation;

public class ValidInputWidgetProp extends ValidatedInput<String>{
	
	private String widgetPropId;
	private String tag = "/{widgetPropId}";
	
	public ValidInputWidgetProp() {
	}
	
	public ValidInputWidgetProp(String widgetPropId) {
		this.widgetPropId = widgetPropId;
	}

	@Override
	public String getValue() {
		return widgetPropId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
