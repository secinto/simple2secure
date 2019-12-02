package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputTestResult extends ValidatedInput<String>{
	
	private String testResultId;
	private String tag = "/{testResultId}";
	
	public ValidInputTestResult() {
	}
	
	public ValidInputTestResult(String testResultId) {
		this.testResultId = testResultId;
	}

	@Override
	public String getValue() {
		return testResultId;
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
		return new ValidInputTestResult(value);
	}
}
