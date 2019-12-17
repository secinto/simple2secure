package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputSearchQuery extends ValidatedInput<String> {

	private String searchQuery;
	private String tag = "/{searchQuery}";

	public ValidInputSearchQuery() {
	}

	public ValidInputSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@Override
	public String getValue() {
		return searchQuery;
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
		return new ValidInputSearchQuery(value);
	}
}
