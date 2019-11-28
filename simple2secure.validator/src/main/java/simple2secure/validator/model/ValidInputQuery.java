package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputQuery extends ValidatedInput<String>{
	
	private String queryId;
	private String tag = "/{queryId}";
	
	public ValidInputQuery() {
	}
	
	public ValidInputQuery(String queryId) {
		this.queryId = queryId;
	}

	@Override
	public String getValue() {
		return queryId;
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
