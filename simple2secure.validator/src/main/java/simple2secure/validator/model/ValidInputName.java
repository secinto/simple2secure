package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputName extends ValidatedInput<String> {

	private String name;
	private String tag = "/{name}";

	public ValidInputName() {
	}

	public ValidInputName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return name;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO: check how to use the repository
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputName(value);
	}
}
