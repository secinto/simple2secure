package com.simple2secure.api.model.validation;

public class ValidInputToken extends ValidatedInput<String>{
	
	private String token;
	private String tag = "/{token}";
	
	public ValidInputToken() {
	}
	
	public ValidInputToken(String token) {
		this.token = token;
	}

	@Override
	public String getValue() {
		return token;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
