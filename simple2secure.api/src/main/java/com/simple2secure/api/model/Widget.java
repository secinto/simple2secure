package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Widget extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3052204613738700648L;
	
	private String name;
	private String description;
	private String tag;
	private String bgClass;
	private String icon;
	private String label;

	public Widget() {
		
	}
	
	public Widget(String name, String description, String tag, String bgClass, String icon, String label) {
		super();
		this.name = name;
		this.description = description;
		this.tag = tag;
		this.bgClass = bgClass;
		this.icon = icon;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getBgClass() {
		return bgClass;
	}

	public void setBgClass(String bgClass) {
		this.bgClass = bgClass;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
