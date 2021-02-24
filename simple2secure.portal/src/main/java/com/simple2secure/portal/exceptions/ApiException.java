package com.simple2secure.portal.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiException {
	private final String errorMessage;
	private final Throwable throwable;
	private final HttpStatus httpStatus;
	private final Date timestamp;
}
