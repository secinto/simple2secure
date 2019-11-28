package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public abstract class ValidatedInput<T> {
	
	public abstract T getValue();
	
	public abstract String getTag();
	
	public abstract Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request);
}
