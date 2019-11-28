package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

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

	@Override
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
