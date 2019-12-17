package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputDevice extends ValidatedInput<String> {

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
		return new ValidInputDevice(value);
	}
}
