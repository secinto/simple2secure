package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

public abstract class ValidatedInput<T> {

	public abstract Object getValue();

	public abstract String getTag();

	public abstract Object validate(HttpServletRequest request, Map<String, Object> params);

	public abstract Object validatePathVariable(T value);
}
