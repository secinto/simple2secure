package com.simple2secure.api.model.validation;

public class ValidInputRule extends ValidatedInput<String>{
	
	private String ruleId;
	private String tag = "/{ruleId}";
	
	public ValidInputRule() {
		
	}
	
	public ValidInputRule(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String getValue() {
		return ruleId;
	}

	@Override
	public void setValue(String value) {
		this.ruleId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
