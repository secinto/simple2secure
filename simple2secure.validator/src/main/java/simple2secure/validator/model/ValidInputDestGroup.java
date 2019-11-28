package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputDestGroup extends ValidatedInput<String>{
	
	private String destGroupId;
	private String tag = "/{destGroupId}";
	
	public ValidInputDestGroup() {
	}
	
	public ValidInputDestGroup(String destGroupId) {
		this.destGroupId = destGroupId;
	}

	@Override
	public String getValue() {
		return destGroupId;
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
