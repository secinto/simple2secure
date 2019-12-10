package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputTestResult extends ValidatedInput<String> {

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
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputTestResult(value);
	}
}
