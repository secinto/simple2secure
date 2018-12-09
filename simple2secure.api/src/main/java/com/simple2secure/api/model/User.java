package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class User extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -4145217947046921778L;

	private String email;
	private String password;
	private boolean enabled;
	private boolean passwordUpdated;
	private String activationToken;
	private boolean activated;
	private String passwordResetToken;
	private long passwordResetExpirationTime;

	public User() {

	}

	public User(String email, String password, boolean enabled, String activationToken, boolean activated, boolean passwordUpdated) {
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.activationToken = activationToken;
		this.activated = activated;
		this.passwordUpdated = passwordUpdated;
	}

	public User(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setActivated(boolean activate) {
		activated = activate;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivationToken(String activationLink) {
		activationToken = activationLink;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setPasswordResetExpirationTime(long timestamp) {
		passwordResetExpirationTime = timestamp;
	}

	public long getPasswordResetExpirationTime() {
		return passwordResetExpirationTime;
	}

	public void setPasswordResetToken(String token) {
		passwordResetToken = token;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public boolean isPasswordUpdated() {
		return passwordUpdated;
	}

	public void setPasswordUpdated(boolean passwordUpdated) {
		this.passwordUpdated = passwordUpdated;
	}
}
