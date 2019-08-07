package com.simple2secure.probe.dao.impl;

import java.util.List;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.probe.dao.LicenseDao;

public class LicenseDaoImpl extends BaseDaoImpl<CompanyLicensePublic> implements LicenseDao {

	public LicenseDaoImpl(String persistenceUnitName) {
		entityClass = CompanyLicensePublic.class;
		if (!Strings.isNullOrEmpty(persistenceUnitName)) {
			init(persistenceUnitName);
		} else {
			init(BaseDaoImpl.PERSISTENCE_UNIT_NAME);
		}
	}

	@Override
	public CompanyLicensePublic getLicense() {
		List<CompanyLicensePublic> licenses = getAll();

		if (licenses.size() != 1) {
			return null;
		} else {
			return licenses.get(0);
		}
	}
}
