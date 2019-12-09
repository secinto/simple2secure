package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class LicenseRepositoryImpl extends LicenseRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "license";
		super.className = CompanyLicensePrivate.class;
	}

	@Override
	public List<CompanyLicensePrivate> findAllByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByGroupIdAndDeviceType(String groupId, boolean deviceIsPod) {
		Query query = new Query(Criteria.where("groupId").is(groupId).and("deviceIsPod").is(deviceIsPod));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByLicenseId(String licenseId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByDeviceStatusOnline() {
		Query query = new Query(Criteria.where("status").is("ONLINE"));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByLicenseIdAndDeviceId(String licenseId, String deviceId, boolean deviceIsPod) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("deviceId").is(deviceId).and("deviceIsPod").is(deviceIsPod));
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
	public CompanyLicensePrivate findByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public void deleteByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<CompanyLicensePrivate> licenses = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);

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
	public void deleteByDeviceId(String deviceId) {
		if (!Strings.isNullOrEmpty(deviceId)) {
			CompanyLicensePrivate license = findByDeviceId(deviceId);
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

	@Override
	public CompanyLicensePrivate findByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public Map<String, Object> findByListOfGroupIdsAndDeviceType(List<String> groupIds, boolean deviceIsPod, int page, int size) {
		List<CompanyLicensePrivate> licenses = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String groupId : groupIds) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(Criteria.where("deviceIsPod").is(deviceIsPod));
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		long count = mongoTemplate.count(query, OsQueryReport.class, collectionName);
		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "lastOnlineTimestamp"));

		licenses = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);

		Map<String, Object> licensesMap = new HashMap<>();
		licensesMap.put("licenses", licenses);
		licensesMap.put("totalSize", count);

		return licensesMap;
	}
}
