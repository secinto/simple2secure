package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTestMacro extends ValidatedInput<String> {

	private ObjectId testMacroId;
	private String tag = "/{testMacroId}";

	public ValidInputTestMacro() {
	}

	public ValidInputTestMacro(String testMacroId) {
		this.testMacroId = new ObjectId(testMacroId);
	}

	@Override
	public ObjectId getValue() {
		return testMacroId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputTestMacro(value);
	}
}
