package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputContext extends ValidatedInput<String>{

	private String contextId;
	private String tag = "/{contextId}";
	
	public ValidInputContext() {
	}
	
	public ValidInputContext(String contextId) {
		this.contextId = contextId;
	}

	@Override
	public String getValue() {
		return contextId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		//TODO: check how to use the repository
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
