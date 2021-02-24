package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputDevice extends ValidatedInput<String> {

	private ObjectId deviceId;
	private String tag = "/{deviceId}";

	public ValidInputDevice() {
	}

	public ValidInputDevice(String deviceId) {
		this.deviceId = new ObjectId(deviceId);
	}

	@Override
	public ObjectId getValue() {
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
