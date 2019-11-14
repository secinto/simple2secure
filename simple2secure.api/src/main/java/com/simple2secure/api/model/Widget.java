package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Widget extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3052204613738700648L;
	
	private String name;
	private String description;
	private String startTag;
	private String closingTag;
	private String bgClass;
	private String icon;
	private String label;

	public Widget() {
		
	}
	
	public Widget(String name, String description, String startTag, String closingTag, String bgClass, String icon, String label) {
		super();
		this.name = name;
		this.description = description;
		this.startTag = startTag;
		this.closingTag = closingTag;
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

	public String getStartTag() {
		return startTag;
	}

	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}

	public String getClosingTag() {
		return closingTag;
	}

	public void setClosingTag(String closingTag) {
		this.closingTag = closingTag;
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
