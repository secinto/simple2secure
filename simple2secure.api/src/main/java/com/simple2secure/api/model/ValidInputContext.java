package com.simple2secure.api.model;

public class ValidInputContext extends ValidatedInput{
	
	private String contextId;
	
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
	public Object validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
