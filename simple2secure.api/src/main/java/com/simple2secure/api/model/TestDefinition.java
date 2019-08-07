package com.simple2secure.api.model;

public class TestDefinition {
	private String description;
	private String version;
	private TestStep precondition;
	private TestStep step;
	private TestStep postcondition;

	public TestDefinition() {

	}

	public TestDefinition(String description, String version, TestStep precondition, TestStep step, TestStep postcondition) {
		super();
		this.description = description;
		this.version = version;
		this.precondition = precondition;
		this.step = step;
		this.postcondition = postcondition;
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
