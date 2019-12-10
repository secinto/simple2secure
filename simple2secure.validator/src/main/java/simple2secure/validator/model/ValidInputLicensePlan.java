package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputLicensePlan extends ValidatedInput<String> {

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
