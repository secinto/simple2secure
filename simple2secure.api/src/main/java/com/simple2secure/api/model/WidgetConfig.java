package com.simple2secure.api.model;

import java.util.List;

public class WidgetConfig {
	private List<String> widgetApis;
	private List<String> widgetTags;
	
	public WidgetConfig() {
		
	}
	
	public WidgetConfig(List<String> widgetApis, List<String> widgetTags) {
		this.widgetApis = widgetApis;
		this.widgetTags = widgetTags;
	}

	public List<String> getWidgetApis() {
		return widgetApis;
	}

	public void setWidgetApis(List<String> widgetApis) {
		this.widgetApis = widgetApis;
	}

	public List<String> getWidgetTags() {
		return widgetTags;
	}

	public void setWidgetTags(List<String> widgetTags) {
		this.widgetTags = widgetTags;
	}
}
