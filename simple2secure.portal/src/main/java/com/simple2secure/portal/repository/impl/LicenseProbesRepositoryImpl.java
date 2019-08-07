package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.simple2secure.portal.model.CompanyLicenseProbes;
import com.simple2secure.portal.repository.LicenseProbesRepository;

public class LicenseProbesRepositoryImpl extends LicenseProbesRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "licenseProbes"; //$NON-NLS-1$
		super.className = CompanyLicenseProbes.class;
	}

	@Override
	public CompanyLicenseProbes findByLicenseId(String licenseId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId));
		return mongoTemplate.findOne(query, CompanyLicenseProbes.class, collectionName);
	}

}
