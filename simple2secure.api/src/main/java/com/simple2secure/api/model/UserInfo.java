package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class UserInfo extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6119414795382021189L;

	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String city;
	private String zip;
	private String company;
	private String mobile;
	private String phone;

	public UserInfo() {
	}

	public UserInfo(String userId, String email) {
		this.userId = userId;
		this.email = email;
	}

	public UserInfo(String userId, String firstName, String lastName, String email, String address, String city, String zip, String company,
			String mobile, String phone) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.address = address;
		this.city = city;
		this.zip = zip;
		this.company = company;
		this.mobile = mobile;
		this.phone = phone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
