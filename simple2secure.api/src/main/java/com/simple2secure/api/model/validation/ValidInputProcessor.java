package com.simple2secure.api.model.validation;

public class ValidInputProcessor extends ValidatedInput<String>{
	
	private String processorId;
	private String tag = "/{processorId}";
	
	public ValidInputProcessor() {
	}
	
	public ValidInputProcessor(String processorId) {
		this.processorId = processorId;
	}

	@Override
	public String getValue() {
		return processorId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
