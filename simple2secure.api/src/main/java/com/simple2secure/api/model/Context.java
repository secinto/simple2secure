package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Context extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5969701189053689561L;

	private String name;

	private String licensePlanId;

	private int currentNumberOfLicenseDownloads;

	public Context() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicensePlanId() {
		return licensePlanId;
	}

	public void setLicensePlanId(String licensePlanId) {
		this.licensePlanId = licensePlanId;
	}

	public int getCurrentNumberOfLicenseDownloads() {
		return currentNumberOfLicenseDownloads;
	}

	public void setCurrentNumberOfLicenseDownloads(int currentNumberOfLicenseDownloads) {
		this.currentNumberOfLicenseDownloads = currentNumberOfLicenseDownloads;
	}

}
