package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "CompanyLicenseObj")
public class CompanyLicensePublic extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7011645066859754490L;

	protected String groupId;

	protected String probeId;

	protected String licenseId;

	@Lob
	protected String accessToken;

	protected String expirationDate;

	protected boolean activated;

	public CompanyLicensePublic() {
	}

	public CompanyLicensePublic(String groupId, String probeId, String licenseId, String expirationDate) {
		this.groupId = groupId;
		this.probeId = probeId;
		this.licenseId = licenseId;
		this.expirationDate = expirationDate;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

}
