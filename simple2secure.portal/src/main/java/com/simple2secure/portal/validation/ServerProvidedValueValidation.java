package com.simple2secure.portal.validation;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.security.auth.DeviceAuthenticationService;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;

@Component
@Slf4j
public class ServerProvidedValueValidation implements HandlerMethodArgumentResolver {

	@Autowired
	DeviceAuthenticationService tokenAuthenticationService;

	@Autowired
	CurrentContextRepository currentContextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	OsQueryReportRepository reportRepository;

	@Autowired
	GroupRepository groupRepository;

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(ServerProvidedValue.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

		HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
		KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();

		if (methodParameter.getGenericParameterType().equals(ValidInputContext.class)) {
			return validateContextInputParameter(principal, methodParameter);
		} else if (methodParameter.getGenericParameterType().equals(ValidInputUser.class)) {

			Map<String, Object> params = new TreeMap<>();
			params.put(ValidInputUser.IS_AUTHENTICATED_TAG, principal.isAuthenticated());
			params.put(ValidInputUser.USER_OBJECT_TAG, principal.getName());
			return new ValidInputUser().validate(request, params);
		} else if (methodParameter.getGenericParameterType().equals(ValidInputLocale.class)) {
			return new ValidInputLocale().validate(request, null);
		}

		return null;
	}

	public ValidInputContext validateContextInputParameter(KeycloakAuthenticationToken auth, MethodParameter methodParameter) {
		if (auth.isAuthenticated()) {
			if (auth.getPrincipal() != null) {
				CurrentContext currentContext = currentContextRepository.findByUserId(auth.getName());
				if (currentContext != null) {
					ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository
							.find(currentContext.getContextUserAuthenticationId());
					if (contextUserAuthentication != null) {
						return new ValidInputContext(contextUserAuthentication.getContextId().toHexString());
					}
				}
			}
		}
		// TODO: Throw an exception that user is not authenticated
		return null;
	}

}
