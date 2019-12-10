package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputVersion extends ValidatedInput<String> {

	private String version;
	private String tag = "/{version}";

	public ValidInputVersion() {
	}

	public ValidInputVersion(String version) {
		this.version = version;
	}

	@Override
	public String getValue() {
		return version;
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
		return new ValidInputVersion(value);
	}
}
