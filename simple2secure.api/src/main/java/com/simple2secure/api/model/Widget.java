package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Widget extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3052204613738700648L;
	
	private String type;

	public Widget() {
		
	}
	
	public Widget(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
