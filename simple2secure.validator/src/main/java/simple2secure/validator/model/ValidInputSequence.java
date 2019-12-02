package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputSequence extends ValidatedInput<String>{
	
	private String sequenceId;
	private String tag = "/{sequenceId}";
	
	public ValidInputSequence() {
	}
	
	public ValidInputSequence(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	@Override
	public String getValue() {
		return sequenceId;
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
		return new ValidInputSequence(value);
	}
}
