package com.simple2secure.api.model.validation;

public class ValidInputContext extends ValidatedInput<String>{
	
	private String contextId;
	private String tag = "/{contextId}";
	
	public ValidInputContext() {
		
	}
	
	public ValidInputContext(String contextId) {
		this.contextId = contextId;
	}

	@Override
	public String getValue() {
		return contextId;
	}

	@Override
	public void setValue(String value) {
		this.contextId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
