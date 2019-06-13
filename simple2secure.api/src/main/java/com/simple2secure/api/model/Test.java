package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Test extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -914338716345452064L;

	private String podId;
	private String description;
	private String version;
	private TestStep precondition;
	private TestStep step;
	private TestStep postcondition;

	public Test() {

	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
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

	public TestStep getPrecondition() {
		return precondition;
	}

	public void setPrecondition(TestStep precondition) {
		this.precondition = precondition;
	}

	public TestStep getStep() {
		return step;
	}

	public void setStep(TestStep step) {
		this.step = step;
	}

	public TestStep getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(TestStep postcondition) {
		this.postcondition = postcondition;
	}
}
