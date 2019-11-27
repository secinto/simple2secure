package com.simple2secure.api.model.validation;

public class ValidInputSut extends ValidatedInput<String>{
	
	private String sutId;
	private String tag = "/{sutId}";
	
	public ValidInputSut() {
	}
	
	public ValidInputSut(String version) {
		this.sutId = version;
	}

	@Override
	public String getValue() {
		return sutId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
