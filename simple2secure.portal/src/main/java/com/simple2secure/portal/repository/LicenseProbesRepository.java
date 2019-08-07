package com.simple2secure.portal.repository;

import com.simple2secure.portal.dao.MongoRepository;
import com.simple2secure.portal.model.CompanyLicenseProbes;

public abstract class LicenseProbesRepository extends MongoRepository<CompanyLicenseProbes> {
	public abstract CompanyLicenseProbes findByLicenseId(String licenseId);
}
