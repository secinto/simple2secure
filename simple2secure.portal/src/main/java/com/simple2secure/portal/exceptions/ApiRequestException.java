package com.simple2secure.portal.exceptions;

public class ApiRequestException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -7370496125291567874L;

	public ApiRequestException(String message) {
		super(message);
	}

	public ApiRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
