package com.simple2secure.portal.validator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.LocaleLanguage;
import com.simple2secure.api.model.Report;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.ValidInputContext;
import com.simple2secure.api.model.ValidInputLocale;
import com.simple2secure.api.model.ValidInputReport;
import com.simple2secure.api.model.ValidInputUser;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;

@Component
public class InputValidation implements HandlerMethodArgumentResolver {

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	CurrentContextRepository currentContextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	GroupRepository groupRepository;

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(ValidInput.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

		HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
		Authentication auth = tokenAuthenticationService.getAuthentication(request);

		if (methodParameter.getGenericParameterType().equals(ValidInputContext.class)) {
			return validateContextInputParameter(auth, methodParameter);
		} else if (methodParameter.getGenericParameterType().equals(ValidInputUser.class)) {
			return validateUserInputParameter(auth, methodParameter);
		} else if (methodParameter.getGenericParameterType().equals(ValidInputLocale.class)) {
			return validateLocaleHeader(request);
		}

		return null;
	}

	public ValidInputContext validateContextInputParameter(Authentication auth, MethodParameter methodParameter) {
		if (auth.isAuthenticated()) {
			if (auth.getPrincipal() != null) {
				User currentUser = (User) auth.getPrincipal();
				CurrentContext currentContext = currentContextRepository.findByUserId(currentUser.getId());
				if (currentContext != null) {
					ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository
							.find(currentContext.getContextUserAuthenticationId());
					if (contextUserAuthentication != null) {
						return new ValidInputContext(contextUserAuthentication.getContextId());
					}
				}
			}
		}
		// TODO: Throw an exception that user is not authenticated
		return null;
	}

	public ValidInputUser validateUserInputParameter(Authentication auth, MethodParameter methodParameter) {
		if (auth.isAuthenticated()) {
			if (auth.getPrincipal() != null) {
				User currentUser = (User) auth.getPrincipal();
				return new ValidInputUser(currentUser.getId());
			}
		}
		// TODO: Throw an exception that user is not authenticated
		return null;
	}

	public ValidInputReport validateReportInputParameter(Authentication auth, MethodParameter methodParameter) {
		if (auth.isAuthenticated()) {
			if (auth.getPrincipal() != null) {
				User currentUser = (User) auth.getPrincipal();
				String reportId = "";

				Report report = reportRepository.find(reportId);
				CompanyGroup group = groupRepository.find(report.getGroupId());

				if (report != null) {
					CurrentContext currentContext = currentContextRepository.findByUserId(currentUser.getId());
					if (currentContext != null) {
						ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository
								.find(currentContext.getContextUserAuthenticationId());

						if (contextUserAuthentication != null && group != null) {
							if (contextUserAuthentication.getContextId().equals(group.getContextId())) {
								return new ValidInputReport(report.getId());
							}
						}
					}
				}
			}
		}
		// TODO: Throw an exception that user is not authenticated
		return null;
	}

	public ValidInputLocale validateLocaleHeader(HttpServletRequest request) {
		LocaleLanguage lang = LocaleLanguage.valueOfLabel(request.getHeader("Accept-Language"));

		if (lang == null) {
			lang = StaticConfigItems.DEFAULT_LOCALE;
		}

		return new ValidInputLocale(lang.label);
	}

}
