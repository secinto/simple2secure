package com.simple2secure.api.model;

public class ValidInputUser extends ValidatedInput{
	
	private String userId;
	
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
	public Object validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
