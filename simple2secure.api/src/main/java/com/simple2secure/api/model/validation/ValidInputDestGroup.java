package com.simple2secure.api.model.validation;

public class ValidInputDestGroup extends ValidatedInput<String>{
	
	private String groupId;
	private String tag = "/{destGroupId}";
	
	public ValidInputDestGroup() {
		
	}
	
	public ValidInputDestGroup(String groupId) {
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
