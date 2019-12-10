package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ValidInputWidgetProp extends ValidatedInput<String> {

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
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputWidgetProp(value);
	}
}
