package com.simple2secure.api.dto;

import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WidgetDTO {
	private Widget widget;
	private WidgetProperties widgetProperties;
	private Object value;
}
