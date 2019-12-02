package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputProcessor extends ValidatedInput<String>{
	
	private String processorId;
	private String tag = "/{processorId}";
	
	public ValidInputProcessor() {
	}
	
	public ValidInputProcessor(String processorId) {
		this.processorId = processorId;
	}

	@Override
	public String getValue() {
		return processorId;
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
		return new ValidInputProcessor(value);
	}
}
