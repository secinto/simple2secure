package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputVersion extends ValidatedInput<String>{
	
	private String version;
	private String tag = "/{version}";
	
	public ValidInputVersion() {
	}
	
	public ValidInputVersion(String version) {
		this.version = version;
	}

	@Override
	public String getValue() {
		return version;
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
		return new ValidInputVersion(value);
	}
}
