package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputSut extends ValidatedInput<String> {

	private ObjectId sutId;
	private String tag = "/{sutId}";

	public ValidInputSut() {
	}

	public ValidInputSut(String version) {
		sutId = new ObjectId(version);
	}

	@Override
	public ObjectId getValue() {
		return sutId;
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
		return new ValidInputSut(value);
	}
}
