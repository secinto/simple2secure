package com.simple2secure.api.model.validation;

public class ValidInputStep extends ValidatedInput<String>{
	
	private String stepId;
	private String tag = "/{stepId}";
	
	public ValidInputStep() {
	}
	
	public ValidInputStep(String stepId) {
		this.stepId = stepId;
	}

	@Override
	public String getValue() {
		return stepId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
