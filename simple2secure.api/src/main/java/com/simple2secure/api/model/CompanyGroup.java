package com.simple2secure.api.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "CompanyGroup")
public class CompanyGroup extends GenericDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8637103644388176110L;
	
	private String name;
	private String addedByUserId;
	private String owner;
	
	private String licenseExpirationDate;
	
	@OneToMany(cascade = {CascadeType.ALL})
	List<CompanyGroup> children;
	
	private int maxNumberOfLicenseDownloads;
	
	private int currentNumberOfLicenseDownloads;
	
	public CompanyGroup() {}
	
	public CompanyGroup(String name, String owner, String addedByUserId, String licenseExpirationDate, List<CompanyGroup> children) {
		this.name = name;
		this.owner = owner;
		this.addedByUserId = addedByUserId;
		this.children = children;
		this.licenseExpirationDate = licenseExpirationDate;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddedByUserId() {
		return addedByUserId;
	}

	public void setAddedByUserId(String addedByUserId) {
		this.addedByUserId = addedByUserId;
	}

	public List<CompanyGroup> getChildren() {
		return children;
	}

	public void setGroupId(List<CompanyGroup> children) {
		this.children = children;
	}

	public String getLicenseExpirationDate() {
		return licenseExpirationDate;
	}

	public void setLicenseExpirationDate(String licenseExpirationDate) {
		this.licenseExpirationDate = licenseExpirationDate;
	}

	public int getMaxNumberOfLicenseDownloads() {
		return maxNumberOfLicenseDownloads;
	}

	public void setMaxNumberOfLicenseDownloads(int maxNumberOfLicenseDownloads) {
		this.maxNumberOfLicenseDownloads = maxNumberOfLicenseDownloads;
	}

	public int getCurrentNumberOfLicenseDownloads() {
		return currentNumberOfLicenseDownloads;
	}

	public void setCurrentNumberOfLicenseDownloads(int currentNumberOfLicenseDownloads) {
		this.currentNumberOfLicenseDownloads = currentNumberOfLicenseDownloads;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
