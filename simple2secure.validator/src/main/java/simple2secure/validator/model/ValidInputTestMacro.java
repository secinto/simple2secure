package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputTestMacro extends ValidatedInput<String> {

	private String testMacroId;
	private String tag = "/{testMacroId}";

	public ValidInputTestMacro() {
	}

	public ValidInputTestMacro(String testMacroId) {
		this.testMacroId = testMacroId;
	}

	@Override
	public String getValue() {
		return testMacroId;
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
		return new ValidInputTestMacro(value);
	}
}
