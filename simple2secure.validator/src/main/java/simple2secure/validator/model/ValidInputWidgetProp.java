package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputWidgetProp extends ValidatedInput<String>{
	
	private String widgetPropId;
	private String tag = "/{widgetPropId}";
	
	public ValidInputWidgetProp() {
	}
	
	public ValidInputWidgetProp(String widgetPropId) {
		this.widgetPropId = widgetPropId;
	}

	@Override
	public String getValue() {
		return widgetPropId;
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
}
