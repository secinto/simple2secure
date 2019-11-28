package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputRule extends ValidatedInput<String>{
	
	private String ruleId;
	private String tag = "/{ruleId}";
	
	public ValidInputRule() {
	}
	
	public ValidInputRule(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String getValue() {
		return ruleId;
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
