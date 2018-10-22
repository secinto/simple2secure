package com.simple2secure.api.model;

import java.util.Date;

import com.simple2secure.api.dbo.GenericDBObject;

public class Token extends GenericDBObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4546453754611943213L;

	private String userId;
	private String probeId;
	private String accessToken;
	private String refreshToken;
	private Date lastLoginDate;
	
	public Token() {};
	
	public Token(String userId, String probeId, String accessToken, String refreshToken, Date lastLoginDate) {
		this.userId = userId;
		this.probeId = probeId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lastLoginDate = lastLoginDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
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

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
}
