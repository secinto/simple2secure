package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestResultObj extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6378259439098732870L;

	private String postcondition;
	private String precondition;
	private String step;

	public TestResultObj() {
	}

	public TestResultObj(String postcondition, String precondition, String step) {
		this.precondition = precondition;
		this.step = step;
		this.postcondition = postcondition;
	}

	public String getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(String postcondition) {
		this.postcondition = postcondition;
	}

	public String getPrecondition() {
		return precondition;
	}

	public void setPrecondition(String precondition) {
		this.precondition = precondition;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

}
