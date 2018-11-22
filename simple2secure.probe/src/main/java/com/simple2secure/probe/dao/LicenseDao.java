package com.simple2secure.probe.dao;

import com.simple2secure.api.model.CompanyLicensePublic;

public interface LicenseDao extends BaseDao<CompanyLicensePublic> {

	public CompanyLicensePublic getLicense();
}
