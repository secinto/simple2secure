package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputSequence extends ValidatedInput<String> {

	private ObjectId sequenceId;
	private String tag = "/{sequenceId}";

	public ValidInputSequence() {
	}

	public ValidInputSequence(String sequenceId) {
		this.sequenceId = new ObjectId(sequenceId);
	}

	@Override
	public ObjectId getValue() {
		return sequenceId;
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
		return new ValidInputSequence(value);
	}
}
