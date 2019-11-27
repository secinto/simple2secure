package com.simple2secure.api.model.validation;

public class ValidInputSearchQuery extends ValidatedInput<String>{
	
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
}
