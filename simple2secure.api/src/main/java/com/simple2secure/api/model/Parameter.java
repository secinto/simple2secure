package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Parameter extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3568435254329514541L;

	private String description;

	private String prefix;

	private String value;

	private Parameter() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
