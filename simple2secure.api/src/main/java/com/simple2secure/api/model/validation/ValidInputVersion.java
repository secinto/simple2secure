package com.simple2secure.api.model.validation;

public class ValidInputVersion extends ValidatedInput<String>{
	
	private String version;
	private String tag = "/{version}";
	
	public ValidInputVersion() {
	}
	
	public ValidInputVersion(String version) {
		this.version = version;
	}

	@Override
	public String getValue() {
		return version;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
