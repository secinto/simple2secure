package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputQuery extends ValidatedInput<String> {

	private String queryId;
	private String tag = "/{queryId}";

	public ValidInputQuery() {
	}

	public ValidInputQuery(String queryId) {
		this.queryId = queryId;
	}

	@Override
	public String getValue() {
		return queryId;
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
		return new ValidInputQuery(value);
	}
}
