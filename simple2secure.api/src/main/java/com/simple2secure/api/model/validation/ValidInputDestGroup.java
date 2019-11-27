package com.simple2secure.api.model.validation;

public class ValidInputDestGroup extends ValidatedInput<String>{
	
	private String destGroupId;
	private String tag = "/{destGroupId}";
	
	public ValidInputDestGroup() {
	}
	
	public ValidInputDestGroup(String destGroupId) {
		this.destGroupId = destGroupId;
	}

	@Override
	public String getValue() {
		return destGroupId;
	}
	
	@Override
	public String getTag() {
		return tag;
	}
}
