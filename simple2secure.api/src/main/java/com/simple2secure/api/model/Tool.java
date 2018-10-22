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
	private String userUUID;
	@OneToMany
	private List<Test> tests;

	private boolean active;

	public Tool() {
	}

	public Tool(String name, String generatedName, String userUUID, List<Test> tests, boolean active) {
		this.name = name;
		this.generatedName = generatedName;
		this.userUUID = userUUID;
		this.tests = tests;
		this.active = active;
	}

	public String getName() {
		return this.name;
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

	public String getUserUUID() {
		return this.userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public List<Test> getTests() {
		return this.tests;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
