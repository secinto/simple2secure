package com.simple2secure.api.model.validation;

public class ValidInputHostname extends ValidatedInput<String>{
	
	private String hostname;
	private String tag = "/{hostname}";
	
	public ValidInputHostname() {
	}
	
	public ValidInputHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getValue() {
		return hostname;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
