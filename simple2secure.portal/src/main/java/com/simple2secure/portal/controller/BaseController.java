package com.simple2secure.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

public class BaseController extends BaseUtilsProvider {
	private static Logger log = LoggerFactory.getLogger(BaseController.class);

	protected ResponseEntity<?> returnError(String message, ValidInputLocale locale) {
		log.error("Responding with error for message {}", message);
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage(message, locale.getValue())), HttpStatus.NOT_FOUND);

	}
}
