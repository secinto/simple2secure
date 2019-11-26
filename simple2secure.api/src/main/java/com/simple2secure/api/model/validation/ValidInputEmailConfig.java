package com.simple2secure.api.model.validation;

public class ValidInputEmailConfig extends ValidatedInput<String>{
	
	private String emailConfigId;
	private String tag = "/{emailConfigId}";
	
	public ValidInputEmailConfig() {
		
	}
	
	public ValidInputEmailConfig(String emailConfigId) {
		this.emailConfigId = emailConfigId;
	}

	@Override
	public String getValue() {
		return emailConfigId;
	}

	@Override
	public void setValue(String value) {
		this.emailConfigId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
