package com.simple2secure.api.model;

import java.util.List;
import java.util.Map;

public class WidgetConfig {
	private Map<String, String> widgetApis;
	private Map<String, String> widgetTags;
	private List<String> widgetIcons;
	private List<String> widgetColors;
	
	public WidgetConfig() {
		
	}
	
	public WidgetConfig(Map<String, String> widgetApis, Map<String, String> widgetTags, List<String> widgetIcons, List<String> widgetColors) {
		this.widgetApis = widgetApis;
		this.widgetTags = widgetTags;
		this.widgetIcons = widgetIcons;
		this.widgetColors = widgetColors;
	}

	public Map<String, String> getWidgetApis() {
		return widgetApis;
	}

	public void setWidgetApis(Map<String, String> widgetApis) {
		this.widgetApis = widgetApis;
	}

	public Map<String, String> getWidgetTags() {
		return widgetTags;
	}

	public void setWidgetTags(Map<String, String> widgetTags) {
		this.widgetTags = widgetTags;
	}

	public List<String> getWidgetIcons() {
		return widgetIcons;
	}

	public void setWidgetIcons(List<String> widgetIcons) {
		this.widgetIcons = widgetIcons;
	}

	public List<String> getWidgetColors() {
		return widgetColors;
	}

	public void setWidgetColors(List<String> widgetColors) {
		this.widgetColors = widgetColors;
	}
}
