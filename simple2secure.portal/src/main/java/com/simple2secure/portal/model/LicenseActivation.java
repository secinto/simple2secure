package com.simple2secure.portal.model;

public class LicenseActivation {
	private String accessToken = "";
	private String refreshToken = "";
	private boolean success;
	private String message = "";

	public LicenseActivation() {
		super();
	}

	public LicenseActivation(boolean success) {
		this.success = success;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
