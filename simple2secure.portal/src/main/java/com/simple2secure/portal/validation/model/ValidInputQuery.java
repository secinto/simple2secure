package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputQuery extends ValidatedInput<String> {

	private ObjectId queryId;
	private String tag = "/{queryId}";

	public ValidInputQuery() {
	}

	public ValidInputQuery(String queryId) {
		this.queryId = new ObjectId(queryId);
	}

	@Override
	public ObjectId getValue() {
		return queryId;
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
		return new ValidInputQuery(value);
	}
}
