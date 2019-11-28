package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputStep extends ValidatedInput<String>{
	
	private String stepId;
	private String tag = "/{stepId}";
	
	public ValidInputStep() {
	}
	
	public ValidInputStep(String stepId) {
		this.stepId = stepId;
	}

	@Override
	public String getValue() {
		return stepId;
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
