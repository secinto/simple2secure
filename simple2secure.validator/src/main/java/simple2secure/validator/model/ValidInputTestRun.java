package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputTestRun extends ValidatedInput<String>{
	
	private String testRunId;
	private String tag = "/{testRunId}";
	
	public ValidInputTestRun() {
	}
	
	public ValidInputTestRun(String testRunId) {
		this.testRunId = testRunId;
	}

	@Override
	public String getValue() {
		return testRunId;
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
