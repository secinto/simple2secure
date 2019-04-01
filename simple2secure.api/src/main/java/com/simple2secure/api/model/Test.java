package com.simple2secure.api.model;

import java.util.ArrayList;

import com.simple2secure.api.dbo.GenericDBObject;

public class Test extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -914338716345452064L;

	private String description;
	private String version;
	private ArrayList<TestStep> precondition;
	private ArrayList<TestStep> step;
	private ArrayList<TestStep> postcondition;

	public Test() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<TestStep> getPrecondition() {
		return precondition;
	}

	public void setPrecondition(ArrayList<TestStep> precondition) {
		this.precondition = precondition;
	}

	public ArrayList<TestStep> getStep() {
		return step;
	}

	public void setStep(ArrayList<TestStep> step) {
		this.step = step;
	}

	public ArrayList<TestStep> getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(ArrayList<TestStep> postcondition) {
		this.postcondition = postcondition;
	}
}
