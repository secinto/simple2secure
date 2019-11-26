package com.simple2secure.api.model.validation;

public class ValidInputQuery extends ValidatedInput<String>{
	
	private String queryId;
	private String tag = "/{queryId}";
	
	public ValidInputQuery() {
		
	}
	
	public ValidInputQuery(String queryId) {
		this.queryId = queryId;
	}

	@Override
	public String getValue() {
		return queryId;
	}

	@Override
	public void setValue(String value) {
		this.queryId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
