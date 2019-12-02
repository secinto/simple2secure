package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
public class ValidInputName extends ValidatedInput<String>{

	private String name;
	private String tag = "/{name}";
	
	public ValidInputName() {
	}
	
	public ValidInputName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return name;
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
		return new ValidInputName(value);
	}
}
