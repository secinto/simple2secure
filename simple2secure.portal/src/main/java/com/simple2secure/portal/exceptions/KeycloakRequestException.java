package com.simple2secure.portal.exceptions;

public class KeycloakRequestException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -7370496125291567874L;

	public KeycloakRequestException(String message) {
		super(message);
	}

	public KeycloakRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
