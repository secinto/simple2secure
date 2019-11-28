package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputTest extends ValidatedInput<String>{
	
	private String testId;
	private String tag = "/{testId}";
	
	public ValidInputTest() {
	}
	
	public ValidInputTest(String testId) {
		this.testId = testId;
	}

	@Override
	public String getValue() {
		return testId;
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
