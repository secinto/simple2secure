package com.simple2secure.portal.validation.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import simple2secure.validator.model.ValidatedInput;

public class ValidInputLicensePlan extends ValidatedInput<String> {

	private ObjectId licensePlanId;
	private String tag = "/{licensePlanId}";

	public ValidInputLicensePlan() {
	}

	public ValidInputLicensePlan(String licensePlanId) {
		this.licensePlanId = new ObjectId(licensePlanId);
	}

	@Override
	public ObjectId getValue() {
		return licensePlanId;
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
		return new ValidInputLicensePlan(value);
	}
}
