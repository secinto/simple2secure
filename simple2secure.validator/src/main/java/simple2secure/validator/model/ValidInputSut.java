package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputSut extends ValidatedInput<String>{
	
	private String sutId;
	private String tag = "/{sutId}";
	
	public ValidInputSut() {
	}
	
	public ValidInputSut(String version) {
		this.sutId = version;
	}

	@Override
	public String getValue() {
		return sutId;
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
		return new ValidInputSut(value);
	}
}
