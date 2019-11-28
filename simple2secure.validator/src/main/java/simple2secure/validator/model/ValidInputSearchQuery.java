package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputSearchQuery extends ValidatedInput<String>{
	
	private String searchQuery;
	private String tag = "/{searchQuery}";
	
	public ValidInputSearchQuery() {
	}
	
	public ValidInputSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@Override
	public String getValue() {
		return searchQuery;
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
