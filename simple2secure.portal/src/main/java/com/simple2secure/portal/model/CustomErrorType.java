package com.simple2secure.portal.model;

public class CustomErrorType {

	private String error;

	public CustomErrorType(String errorMessage) {
		this.error = errorMessage;
	}

	public String getErrorMessage() {
		return error;
	}

}
