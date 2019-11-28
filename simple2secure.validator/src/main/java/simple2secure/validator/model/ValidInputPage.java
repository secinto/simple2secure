package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputPage extends ValidatedInput<Integer>{
	
	private int page;
	private String tag = "/{page}";
	
	public ValidInputPage() {
	}
	
	public ValidInputPage(int page) {
		this.page = page;
	}

	@Override
	public Integer getValue() {
		return page;
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
