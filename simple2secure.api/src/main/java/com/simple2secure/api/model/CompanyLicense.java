package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "License")
public class CompanyLicense extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8652284780091080199L;

	private String groupId;
	
	private String probeId;
	
	private String userId;
	
	private String tokenSecret;
	
	private String accessToken;
	
	private String expirationDate;
	
	private boolean activated;
	
	public CompanyLicense() {}
	
	public CompanyLicense(String groupId, boolean activated) {
		this.groupId = groupId;
		this.activated = activated;
	}
	
	public CompanyLicense(String groupId, String userId, boolean activated) {
		this.groupId = groupId;
		this.userId = userId;
		this.activated = activated;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
}
