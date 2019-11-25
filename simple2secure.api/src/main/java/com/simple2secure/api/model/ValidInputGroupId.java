package com.simple2secure.api.model;

public class ValidInputGroupId extends ValidatedInput{
	
	private String sourceGroupId;
	
	public ValidInputGroupId(String sourceGroupId) {
		this.sourceGroupId = sourceGroupId;
	}

	@Override
	public String getValue() {
		return sourceGroupId;
	}

	@Override
	public void setValue(String sourceGroupId) {
		this.sourceGroupId = sourceGroupId;
	}
	
}
