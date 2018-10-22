package com.simple2secure.probe.dao;

import com.simple2secure.api.model.CompanyLicenseObj;

public interface LicenseDao extends BaseDao<CompanyLicenseObj> {

	public CompanyLicenseObj getLicense();
}
