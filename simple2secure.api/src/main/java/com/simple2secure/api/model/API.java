package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "API")
public class API extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3081134740537811621L;
	private String name;
	private String url;

	public API() {
	}

	public API(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
