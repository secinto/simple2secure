package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputContext extends ValidatedInput<String> {

	private ObjectId contextId;
	private String tag = "/{contextId}";

	public ValidInputContext() {
	}

	public ValidInputContext(String contextId) {
		this.contextId = new ObjectId(contextId);
	}

	@Override
	public ObjectId getValue() {
		return contextId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO: check how to use the repository
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
