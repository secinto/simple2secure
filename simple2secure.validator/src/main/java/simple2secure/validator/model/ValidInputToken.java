package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputToken extends ValidatedInput<String>{
	
	private String token;
	private String tag = "/{token}";
	
	public ValidInputToken() {
	}
	
	public ValidInputToken(String token) {
		this.token = token;
	}

	@Override
	public String getValue() {
		return token;
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
