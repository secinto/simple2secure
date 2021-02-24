package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputProcessor extends ValidatedInput<String> {

	private ObjectId processorId;
	private String tag = "/{processorId}";

	public ValidInputProcessor() {
	}

	public ValidInputProcessor(String processorId) {
		this.processorId = new ObjectId(processorId);
	}

	@Override
	public ObjectId getValue() {
		return processorId;
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
		return new ValidInputProcessor(value);
	}
}
