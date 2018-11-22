package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "License")
public class CompanyLicensePrivate extends CompanyLicensePublic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8652284780091080199L;

	private String tokenSecret;

	public CompanyLicensePrivate(String groupId, boolean activated) {
		super();
		this.groupId = groupId;
		this.activated = activated;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
}
