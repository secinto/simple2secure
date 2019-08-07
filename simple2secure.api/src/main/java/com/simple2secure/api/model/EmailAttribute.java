package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class EmailAttribute extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -2208957747330744059L;

	private String attribute;
	private EmailAttributeEnum type;

	public EmailAttribute(String attribute, EmailAttributeEnum type) {
		this.attribute = attribute;
		this.type = type;
	}

	public EmailAttribute() {

	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public EmailAttributeEnum getType() {
		return type;
	}

	public void setType(EmailAttributeEnum type) {
		this.type = type;
	}

}
