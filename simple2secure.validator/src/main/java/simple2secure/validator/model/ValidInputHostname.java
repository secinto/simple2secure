package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputHostname extends ValidatedInput<String>{
	
	private String hostname;
	private String tag = "/{hostname}";
	
	public ValidInputHostname() {
	}
	
	public ValidInputHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getValue() {
		return hostname;
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
