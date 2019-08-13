package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Rule extends GenericDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1812440159847822313L;

	private String name;

	private String description;

	private String contextID;

	private String groovyCode;

	public Rule() {

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

	public String getCondextID() {
		return contextID;
	}

	public void setContextID(String contextID) {
		this.contextID = contextID;
	}

	public String getGroovyCode() {
		return groovyCode;
	}

	public void setGroovyCode(String groovyCode) {
		this.groovyCode = groovyCode;
	}

}
