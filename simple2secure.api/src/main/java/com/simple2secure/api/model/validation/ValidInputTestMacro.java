package com.simple2secure.api.model.validation;

public class ValidInputTestMacro extends ValidatedInput<String>{
	
	private String testMacroId;
	private String tag = "/{testMacroId}";
	
	public ValidInputTestMacro() {
	}
	
	public ValidInputTestMacro(String testMacroId) {
		this.testMacroId = testMacroId;
	}

	@Override
	public String getValue() {
		return testMacroId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
