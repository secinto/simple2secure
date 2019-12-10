package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputPage extends ValidatedInput<Integer> {

	private int page;
	private String tag = "/{page}";

	public ValidInputPage() {
	}

	public ValidInputPage(int page) {
		this.page = page;
	}

	@Override
	public Integer getValue() {
		return page;
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
	public Object validatePathVariable(Integer value) {
		return new ValidInputPage(value);
	}
}
