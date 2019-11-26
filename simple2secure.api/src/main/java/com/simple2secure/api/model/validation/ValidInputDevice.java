package com.simple2secure.api.model.validation;

public class ValidInputDevice extends ValidatedInput<String>{
	
	private String deviceId;
	private String tag = "/{deviceId}";
	
	public ValidInputDevice() {
		
	}
	
	public ValidInputDevice(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String getValue() {
		return deviceId;
	}

	@Override
	public void setValue(String value) {
		this.deviceId = value;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
