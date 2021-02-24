package com.simple2secure.api.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WidgetConfig {
	private Map<String, String> widgetApis;
	private Map<String, String> widgetTags;
	private List<String> widgetIcons;
	private List<String> widgetColors;
}
