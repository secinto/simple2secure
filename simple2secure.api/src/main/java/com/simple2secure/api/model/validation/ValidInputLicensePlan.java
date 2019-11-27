package com.simple2secure.api.model.validation;

public class ValidInputLicensePlan extends ValidatedInput<String>{
	
	private String licensePlanId;
	private String tag = "/{licensePlanId}";
	
	public ValidInputLicensePlan() {
	}
	
	public ValidInputLicensePlan(String licensePlanId) {
		this.licensePlanId = licensePlanId;
	}

	@Override
	public String getValue() {
		return licensePlanId;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
