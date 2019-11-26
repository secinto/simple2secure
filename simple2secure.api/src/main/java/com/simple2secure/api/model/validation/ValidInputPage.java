package com.simple2secure.api.model.validation;

public class ValidInputPage extends ValidatedInput<Integer>{
	
	private int page;
	private String tag = "/{page}";
	
	public ValidInputPage() {
		
	}
	
	public ValidInputPage(int deviceId) {
		this.page = deviceId;
	}

	@Override
	public Integer getValue() {
		return page;
	}

	@Override
	public void setValue(Integer value) {
		this.page = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
