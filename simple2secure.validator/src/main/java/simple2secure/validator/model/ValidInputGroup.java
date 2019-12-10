package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputGroup extends ValidatedInput<String> {

	private String groupId;
	private String tag = "/{groupId}";

	public ValidInputGroup() {
	}

	public ValidInputGroup(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getValue() {
		return groupId;
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
		return new ValidInputGroup(value);
	}
}
