package com.simple2secure.api.model.validation;

public class ValidInputUser extends ValidatedInput<String>{
	
	private String userId;
	private String tag = "/{userId}";
	
	public ValidInputUser() {
		
	}
	
	public ValidInputUser(String userId) {
		this.userId = userId;
	}

	@Override
	public String getValue() {
		return userId;
	}

	@Override
	public void setValue(String value) {
		this.userId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
