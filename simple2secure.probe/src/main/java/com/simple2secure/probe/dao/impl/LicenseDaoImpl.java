package com.simple2secure.probe.dao.impl;
import java.util.List;

import com.simple2secure.api.model.CompanyLicenseObj;
import com.simple2secure.probe.dao.LicenseDao;

public class LicenseDaoImpl extends BaseDaoImpl<CompanyLicenseObj> implements LicenseDao {

	public LicenseDaoImpl() {
		entityClass = CompanyLicenseObj.class;
	}

	@Override
	public CompanyLicenseObj getLicense() {
		List<CompanyLicenseObj> licenses = getAll();
		
		if(licenses.size() != 1) {
			return null;
		}
		else {
			return licenses.get(0);
		}		
	}
}
