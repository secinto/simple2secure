package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputSequence extends ValidatedInput<String> {

	private String sequenceId;
	private String tag = "/{sequenceId}";

	public ValidInputSequence() {
	}

	public ValidInputSequence(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	@Override
	public String getValue() {
		return sequenceId;
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
		return new ValidInputSequence(value);
	}
}
