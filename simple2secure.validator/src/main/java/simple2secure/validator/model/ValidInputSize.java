package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.simple2secure.commons.config.StaticConfigItems;

public class ValidInputSize extends ValidatedInput<Integer> {

	private int size;
	private String tag = "/{size}";

	public ValidInputSize() {
	}

	public ValidInputSize(int size) {
		this.size = size;
	}

	@Override
	public Integer getValue() {
		return size;
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
		if (value > StaticConfigItems.MAX_VALUE_SIZE) {
			value = StaticConfigItems.DEFAULT_VALUE_SIZE;
		}
		return new ValidInputSize(value);
	}
}
