package com.simple2secure.portal.validator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.simple2secure.api.model.LocaleLanguage;

public class HeaderLocaleArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(Locale.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

		HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

		LocaleLanguage lang = LocaleLanguage.valueOfLabel(request.getHeader("Accept-Language"));

		if (lang == null) {
			lang = LocaleLanguage.ENGLISH;
		}

		return lang.label;
	}
}
