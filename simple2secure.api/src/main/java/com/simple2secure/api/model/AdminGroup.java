package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class AdminGroup extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5969701189053689561L;

	private String name;

	private List<String> admins = new ArrayList<String>();

	private String licensePlanId;

	private int currentNumberOfLicenseDownloads;

	public AdminGroup() {

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

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

	public void addAdmin(String userId) {
		admins.add(userId);
	}

	public void removeAdmin(String userId) {
		admins.remove(userId);
	}

	public int getCurrentNumberOfLicenseDownloads() {
		return currentNumberOfLicenseDownloads;
	}

	public void setCurrentNumberOfLicenseDownloads(int currentNumberOfLicenseDownloads) {
		this.currentNumberOfLicenseDownloads = currentNumberOfLicenseDownloads;
	}

}
