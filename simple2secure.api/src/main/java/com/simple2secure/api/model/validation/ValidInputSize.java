package com.simple2secure.api.model.validation;

public class ValidInputSize extends ValidatedInput<Integer>{
	
	private int size;
	private String tag = "/{size}";
	
	public ValidInputSize() {
		
	}
	
	public ValidInputSize(int deviceId) {
		this.size = deviceId;
	}

	@Override
	public Integer getValue() {
		return size;
	}

	@Override
	public void setValue(Integer value) {
		this.size = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
