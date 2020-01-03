package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class ValidatedInput<T> {

	public abstract T getValue();

	public abstract String getTag();

	public abstract Object validate(HttpServletRequest request, Map<String, Object> params);

	public abstract Object validatePathVariable(T value);
}
