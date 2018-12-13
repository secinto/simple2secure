package com.simple2secure.api.model;

import java.util.List;

import javax.persistence.OneToMany;

import com.simple2secure.api.dbo.GenericDBObject;

public class Tool extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5117460854837863203L;

	private String name;
	private String generatedName;
	private String contextId;
	@OneToMany
	private List<Test> tests;

	private boolean active;

	public Tool() {
	}

	public Tool(String name, String generatedName, String contextId, List<Test> tests, boolean active) {
		this.name = name;
		this.generatedName = generatedName;
		this.contextId = contextId;
		this.tests = tests;
		this.active = active;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGeneratedName() {
		return generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public List<Test> getTests() {
		return tests;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
