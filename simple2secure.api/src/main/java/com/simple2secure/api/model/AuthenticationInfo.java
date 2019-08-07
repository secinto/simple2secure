package com.simple2secure.api.model;

public class AuthenticationInfo {

	private String podId;

	private String authenticationId;

	private String version;

	private String authenticationToken;

	private String atExpirationDate;

	private String offlineToken;

	private String otExpirationDate;

	private String revocationToken;

	private String contextId;

	private String signature;

	public AuthenticationInfo() {
	}

	public AuthenticationInfo(String podId, String authenticationId, String version, String authenticationToken, String atExpirationDate,
			String offlineToken, String otExpirationDate, String revocationToken, String contextId, String signature) {
		super();
		this.podId = podId;
		this.authenticationId = authenticationId;
		this.version = version;
		this.authenticationToken = authenticationToken;
		this.atExpirationDate = atExpirationDate;
		this.offlineToken = offlineToken;
		this.otExpirationDate = otExpirationDate;
		this.revocationToken = revocationToken;
		this.contextId = contextId;
		this.signature = signature;
	}

	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public String getAtExpirationDate() {
		return atExpirationDate;
	}

	public void setAtExpirationDate(String atExpirationDate) {
		this.atExpirationDate = atExpirationDate;
	}

	public String getOfflineToken() {
		return offlineToken;
	}

	public void setOfflineToken(String offlineToken) {
		this.offlineToken = offlineToken;
	}

	public String getOtExpirationDate() {
		return otExpirationDate;
	}

	public void setOtExpirationDate(String otExpirationDate) {
		this.otExpirationDate = otExpirationDate;
	}

	public String getRevocationToken() {
		return revocationToken;
	}

	public void setRevocationToken(String revocationToken) {
		this.revocationToken = revocationToken;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
