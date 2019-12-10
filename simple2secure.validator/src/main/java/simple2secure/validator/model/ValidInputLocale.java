package simple2secure.validator.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.simple2secure.api.model.LocaleLanguage;
import com.simple2secure.commons.config.StaticConfigItems;

public class ValidInputLocale extends ValidatedInput<String> {

	private String locale;

	public ValidInputLocale() {
	}

	public ValidInputLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String getValue() {
		return locale;
	}

	@Override
	public String getTag() {
		return null;
	}

	@Override
	public Object validate(HttpServletRequest request, Map<String, Object> params) {
		LocaleLanguage lang = LocaleLanguage.valueOfLabel(request.getHeader("Accept-Language"));

		if (lang == null) {
			lang = StaticConfigItems.DEFAULT_LOCALE;
		}

		return new ValidInputLocale(lang.label);
	}

	@Override
	public Object validatePathVariable(String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
