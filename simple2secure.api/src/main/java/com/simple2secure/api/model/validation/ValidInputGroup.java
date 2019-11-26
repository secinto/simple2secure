package com.simple2secure.api.model.validation;

public class ValidInputGroup extends ValidatedInput<String>{
	
	private String groupId;
	private String tag = "/{groupId}";
	
	public ValidInputGroup() {
		
	}
	
	public ValidInputGroup(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getValue() {
		return groupId;
	}

	@Override
	public void setValue(String value) {
		this.groupId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
