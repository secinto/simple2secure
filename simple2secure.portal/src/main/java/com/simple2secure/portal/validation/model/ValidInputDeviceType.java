package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.simple2secure.api.model.DeviceType;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputDeviceType extends ValidatedInput<String> {

	private String deviceType;
	private String tag = "/{deviceType}";

	public ValidInputDeviceType() {
	}

	public ValidInputDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String getValue() {
		return deviceType;
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

		String deviceType = DeviceType.getDeviceType(value).toString();

		return new ValidInputDeviceType(deviceType);
	}
}
