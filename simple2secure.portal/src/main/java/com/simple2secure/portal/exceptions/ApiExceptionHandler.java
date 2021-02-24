package com.simple2secure.portal.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(
			value = ApiRequestException.class)
	public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
		ApiException apiException = new ApiException(e.getMessage(), e, HttpStatus.BAD_REQUEST, new Date());
		return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(
			value = KeycloakRequestException.class)
	public ResponseEntity<Object> handleApiRequestException(KeycloakRequestException e) {
		ApiException apiException = new ApiException(e.getLocalizedMessage(), e, HttpStatus.UNAUTHORIZED, new Date());
		return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
	}
}
