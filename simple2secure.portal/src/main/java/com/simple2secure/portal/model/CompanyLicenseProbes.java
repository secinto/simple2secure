package com.simple2secure.portal.model;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "LicenseProbes")
public class CompanyLicenseProbes extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5768517712595086763L;

	private String licenseId;
	private Set<String> activatedProbes;

	public CompanyLicenseProbes() {
		activatedProbes = new TreeSet<String>();
	}

	public void addProbe(String probeId) {
		activatedProbes.add(probeId);
	}

	public void removeProbe(String probeId) {
		activatedProbes.remove(probeId);
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

}
