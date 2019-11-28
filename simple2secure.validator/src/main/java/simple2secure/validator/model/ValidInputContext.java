package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.User;

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
}
