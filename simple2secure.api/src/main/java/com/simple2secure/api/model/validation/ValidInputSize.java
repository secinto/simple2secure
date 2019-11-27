package com.simple2secure.api.model.validation;

public class ValidInputSize extends ValidatedInput<Integer>{
	
	private int size;
	private String tag = "/{size}";
	
	public ValidInputSize() {
	}
	
	public ValidInputSize(int size) {
		this.size = size;
	}

	@Override
	public Integer getValue() {
		return size;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
