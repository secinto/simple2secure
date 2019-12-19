package com.simple2secure.portal.providers;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.model.ApiError;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseServiceProvider extends BaseRepositoryProvider {

	/*
	 * Special services
	 */

	@Autowired
	public MessageByLocaleService messageByLocaleService;

	@Autowired
	public LoadedConfigItems loadedConfigItems;

	@Autowired
	public DataInitialization dataInitialization;

	@Autowired
	public ErrorAttributes errorAttributes;

	@Autowired
	public TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	public PasswordEncoder passwordEncoder;

	@Autowired
	public PasswordValidator passwordValidator;

	protected ResponseEntity<?> buildResponseEntity(String message, ValidInputLocale locale) {
		log.error("Responding with error for message {}", message);

		ApiError apiError = new ApiError();

		String generatedMessage = messageByLocaleService.getMessage(message, locale.getValue());

		if (Strings.isNullOrEmpty(generatedMessage)) {
			generatedMessage = message;
		}

		apiError.setErrorMessage(generatedMessage);
		apiError.setStatus(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}
}
