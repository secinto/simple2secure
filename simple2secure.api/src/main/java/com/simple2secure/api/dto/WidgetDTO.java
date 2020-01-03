package com.simple2secure.api.dto;

import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetProperties;

public class WidgetDTO {
	private Widget widget;
	private WidgetProperties widgetProperties;
	private Object value;

	public WidgetDTO() {
	}

	public WidgetDTO(Widget widget, WidgetProperties widgetProperties, Object value) {
		super();
		this.widget = widget;
		this.widgetProperties = widgetProperties;
		this.value = value;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public WidgetProperties getWidgetProperties() {
		return widgetProperties;
	}

	public void setWidgetProperties(WidgetProperties widgetProperties) {
		this.widgetProperties = widgetProperties;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	};
}
