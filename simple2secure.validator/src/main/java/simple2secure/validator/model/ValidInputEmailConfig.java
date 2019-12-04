package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputEmailConfig extends ValidatedInput<String>{
	
	private String emailConfigId;
	private String tag = "/{emailConfigId}";
	
	public ValidInputEmailConfig() {
	}
	
	public ValidInputEmailConfig(String emailConfigId) {
		this.emailConfigId = emailConfigId;
	}

	@Override
	public String getValue() {
		return emailConfigId;
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

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputEmailConfig(value);
	}
}
