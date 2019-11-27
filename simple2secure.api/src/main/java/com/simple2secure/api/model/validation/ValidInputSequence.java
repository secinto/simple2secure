package com.simple2secure.api.model.validation;

public class ValidInputSequence extends ValidatedInput<String>{
	
	private String sequenceId;
	private String tag = "/{sequenceId}";
	
	public ValidInputSequence() {
	}
	
	public ValidInputSequence(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	@Override
	public String getValue() {
		return sequenceId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
