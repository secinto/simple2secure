package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputWidget extends ValidatedInput<String>{
	
	private String widgetId;
	private String tag = "/{widgetId}";
	
	public ValidInputWidget() {
	}
	
	public ValidInputWidget(String widgetId) {
		this.widgetId = widgetId;
	}

	@Override
	public String getValue() {
		return widgetId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputWidget(value);
	}
}
