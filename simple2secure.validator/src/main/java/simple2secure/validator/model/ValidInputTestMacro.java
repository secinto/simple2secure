package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputTestMacro extends ValidatedInput<String>{
	
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
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
