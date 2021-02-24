package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputTestDataInput extends ValidatedInput<String> {

	private ObjectId inputDataId;
	private String tag = "/{inputDataId}";

	public ValidInputTestDataInput() {
	}

	public ValidInputTestDataInput(String inputDataId) {
		this.inputDataId = new ObjectId(inputDataId);
	}

	@Override
	public ObjectId getValue() {
		return inputDataId;
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
		return new ValidInputTestDataInput(value);
	}
}
