package com.simple2secure.probe.dao.impl;
import java.util.List;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.dao.LicenseDao;

public class LicenseDaoImpl extends BaseDaoImpl<CompanyLicensePublic> implements LicenseDao {

	public LicenseDaoImpl() {
		entityClass = CompanyLicensePublic.class;
	}

	@Override
	public CompanyLicensePublic getLicense() {
		List<CompanyLicensePublic> licenses = getAll();
		
		if(licenses.size() != 1) {
			return null;
		}
		else {
			return licenses.get(0);
		}		
	}
}
