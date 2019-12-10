package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputToken extends ValidatedInput<String> {

	private String token;
	private String tag = "/{token}";

	public ValidInputToken() {
	}

	public ValidInputToken(String token) {
		this.token = token;
	}

	@Override
	public String getValue() {
		return token;
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
		return new ValidInputToken(value);
	}
}
