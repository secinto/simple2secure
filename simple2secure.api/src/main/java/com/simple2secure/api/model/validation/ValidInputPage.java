package com.simple2secure.api.model.validation;

public class ValidInputPage extends ValidatedInput<Integer>{
	
	private int page;
	private String tag = "/{page}";
	
	public ValidInputPage() {
	}
	
	public ValidInputPage(int page) {
		this.page = page;
	}

	@Override
	public Integer getValue() {
		return page;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
