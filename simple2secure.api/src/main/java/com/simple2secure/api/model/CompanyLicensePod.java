package com.simple2secure.api.model;

public class CompanyLicensePod extends CompanyLicensePublic {

	/**
	 *
	 */
	private static final long serialVersionUID = -2403298317875341689L;
	private String configuration;

	public CompanyLicensePod() {
		// TODO Auto-generated constructor stub
	}

	public CompanyLicensePod(String configuration) {
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

}
