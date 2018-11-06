package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import com.simple2secure.api.dbo.GenericDBObject;

public class LicensePlan extends GenericDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3676662699255701433L;
	
	private String name;
	private long validity;
	private TimeUnit validityUnit;
	private int maxNumberOfDownloads;
	
	public LicensePlan() {
		
	}
	
	public LicensePlan(String name, long validity, TimeUnit validityUnit, int maxNumberOfDownloads) {
		this.name = name;
		this.validity = validity;
		this.validityUnit = validityUnit;
		this.maxNumberOfDownloads = maxNumberOfDownloads;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long validity) {
		this.validity = validity;
	}

	public TimeUnit getValidityUnit() {
		return validityUnit;
	}

	public void setValidityUnit(TimeUnit validityUnit) {
		this.validityUnit = validityUnit;
	}

	public int getMaxNumberOfDownloads() {
		return maxNumberOfDownloads;
	}

	public void setMaxNumberOfDownloads(int maxNumberOfDownloads) {
		this.maxNumberOfDownloads = maxNumberOfDownloads;
	}
}
