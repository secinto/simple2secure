package com.simple2secure.api.model;

public class PodToken {

	private String authToken;
	private String podToken;

	public PodToken() {
	}

	public PodToken(String authToken, String podToken) {
		this.authToken = authToken;
		this.podToken = podToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getPodToken() {
		return podToken;
	}

	public void setPodToken(String podToken) {
		this.podToken = podToken;
	}
}
