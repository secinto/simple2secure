package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class WidgetUserRelation extends GenericDBObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1546061720536100889L;
	
	private String userId;
	private String widgetId;
	private String widgetPropertiesId;
	
	public WidgetUserRelation() {}
	
	public WidgetUserRelation(String userId, String widgetId, String widgetPropertiesId) {
		super();
		this.userId = userId;
		this.widgetId = widgetId;
		this.widgetPropertiesId = widgetPropertiesId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}
	public String getWidgetPropertiesId() {
		return widgetPropertiesId;
	}
	public void setWidgetPropertiesId(String widgetPropertiesId) {
		this.widgetPropertiesId = widgetPropertiesId;
	}
	
	
}
