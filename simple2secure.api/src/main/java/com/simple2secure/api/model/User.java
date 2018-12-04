package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class User extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -4145217947046921778L;

	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String password;
	private String address;
	private String city;
	private String zip;
	private String company;
	private String mobile;
	private String phone;
	private boolean enabled;
	private boolean passwordUpdated;
	private String activationToken;
	private boolean activated;
	private String passwordResetToken;
	private long passwordResetExpirationTime;

	public User() {

	}

	public User(String firstName, String lastName, String username, String email, String password, boolean enabled, UserRole userRole,
			String activationToken, boolean activated, boolean passwordUpdated) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.activationToken = activationToken;
		this.activated = activated;
		this.passwordUpdated = passwordUpdated;
	}

	public User(String email, UserRole userRole) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
