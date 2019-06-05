package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.portal.repository.LicenseRepository;

@Repository
@Transactional
public class LicenseRepositoryImpl extends LicenseRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "license";
		super.className = CompanyLicensePrivate.class;
	}

	@Override
	public List<CompanyLicensePrivate> findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByLicenseId(String licenseId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByLicenseIdAndProbeId(String licenseId, String probeId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("probeId").is(probeId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByGroupAndUserId(String groupId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("groupId").is(groupId));
		List<CompanyLicensePrivate> license = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);

		if (license != null && license.size() == 1) {
			return license.get(0);
		} else {
			return null;
		}
	}

	@Override
	public CompanyLicensePrivate findByProbeId(String probeId) {
		Query query = new Query(Criteria.where("probeId").is(probeId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByPodId(String podId) {
		Query query = new Query(Criteria.where("podId").is(podId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public void deleteByGroupId(String groupId) {
		List<CompanyLicensePrivate> licenses = findByGroupId(groupId);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				this.delete(license);
			}
		}

	}

	@Override
	public CompanyLicensePrivate findByAccessToken(String accessToken) {
		Query query = new Query(Criteria.where("accessToken").is(accessToken));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public void deleteByProbeId(String probeId) {
		if (!Strings.isNullOrEmpty(probeId)) {
			CompanyLicensePrivate license = findByProbeId(probeId);
			if (license != null) {
				delete(license);
			}
		}

	}

	@Override
	public CompanyLicensePrivate findByLicenseIdAndPodId(String licenseId, String podId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("podId").is(podId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public void deleteByPodId(String podId) {
		// TODO Auto-generated method stub
		if (!Strings.isNullOrEmpty(podId)) {
			CompanyLicensePrivate license = findByPodId(podId);
			if (license != null) {
				delete(license);
			}
		}
	}

	@Override
	public CompanyLicensePrivate findByLicenseAndHostname(String licenseId, String hostname) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("hostname").is(hostname));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}
}
