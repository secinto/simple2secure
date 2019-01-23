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

	public CompanyLicensePrivate() {
	};

	public CompanyLicensePrivate(String groupId, String licenseId, String expirationDate, boolean activated) {
		super(groupId, licenseId, expirationDate);
		this.activated = activated;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	/**
	 * Function returns the {@link CompanyLicensePublic} object for this {@link CompanyLicensePrivate} object. It cleans all sensitive
	 * information such that serializing this object doesn't leak any information.
	 *
	 * @return
	 */
	public CompanyLicensePublic getPublicLicense() {
		CompanyLicensePublic publicLicense = new CompanyLicensePublic(groupId, licenseId, expirationDate, probeId);
		publicLicense.setActivated(activated);
		publicLicense.setAccessToken(accessToken);
		return publicLicense;
	}

	public CompanyLicensePrivate copyLicense() {
		CompanyLicensePrivate license = new CompanyLicensePrivate(groupId, licenseId, expirationDate, false);
		return license;
	}
}
