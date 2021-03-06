package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.commons.config.StaticConfigItems;
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
	public List<CompanyLicensePrivate> findAllByGroupId(ObjectId groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByLicenseId(ObjectId licenseId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByDeviceStatusOnline() {
		Query query = new Query(Criteria.where("status").is("ONLINE"));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByLicenseIdAndDeviceId(ObjectId licenseId, ObjectId deviceId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("deviceId").is(deviceId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public List<CompanyLicensePrivate> findByUserId(ObjectId userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByGroupAndUserId(ObjectId groupId, ObjectId userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("groupId").is(groupId));
		List<CompanyLicensePrivate> license = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);

		if (license != null && license.size() == 1) {
			return license.get(0);
		} else {
			return null;
		}
	}

	@Override
	public CompanyLicensePrivate findByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public void deleteByGroupId(ObjectId groupId) {
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
	public void deleteByDeviceId(ObjectId deviceId) {
		if (deviceId != null) {
			CompanyLicensePrivate license = findByDeviceId(deviceId);
			if (license != null) {
				delete(license);
			}
		}

	}

	@Override
	public CompanyLicensePrivate findByLicenseAndHostname(ObjectId licenseId, String hostname) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId).and("hostname").is(hostname));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public CompanyLicensePrivate findByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		return mongoTemplate.findOne(query, CompanyLicensePrivate.class, collectionName);
	}

	@Override
	public Map<String, Object> findByGroupIdsPaged(List<ObjectId> groupIds, int page, int size) {
		List<CompanyLicensePrivate> licenses = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (ObjectId groupId : groupIds) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		/*
		 * if (deviceIsPod) { query.addCriteria(Criteria.where("deviceIsPod").is(deviceIsPod)); }
		 */

		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		long count = mongoTemplate.count(query, OsQueryReport.class, collectionName);
		if (size != -1) {
			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
		}
		query.with(Sort.by(Sort.Direction.DESC, "lastOnlineTimestamp"));

		licenses = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);

		Map<String, Object> licensesMap = new HashMap<>();
		licensesMap.put("licenses", licenses);
		licensesMap.put("totalSize", count);

		return licensesMap;
	}

	@Override
	public List<CompanyLicensePrivate> findByGroupIds(List<ObjectId> groupIds) {
		List<CompanyLicensePrivate> licenses = new ArrayList<>();
		if (!groupIds.isEmpty()) {
			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();
			for (ObjectId groupId : groupIds) {
				Criteria expression = new Criteria();
				expression.and("groupId").is(groupId);
				orExpression.add(expression);
			}
			// query.addCriteria(Criteria.where("deviceIsPod").is(deviceIsPod));
			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
			licenses = mongoTemplate.find(query, CompanyLicensePrivate.class, collectionName);
		}

		return licenses;
	}

	@Override
	public Map<String, Object> getDevicesByGroupIdPagination(ObjectId groupId, int page, int size, String filter) {

		AggregationOperation matchGroupId = Aggregation.match(new Criteria("groupId").is(groupId));
		AggregationOperation lookUpDevice = Aggregation.lookup("deviceInfo", "deviceId", "_id", "info");
		AggregationOperation lookUpGroup = Aggregation.lookup("companygroup", "groupId", "_id", "group");
		AggregationOperation unwindInfo = Aggregation.unwind("$info");
		AggregationOperation unwindGroup = Aggregation.unwind("$group");
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);

		String[] filterFields = { "info.name", "info.type", "info.deviceStatus" };

		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));

		Aggregation aggregation = Aggregation.newAggregation(CompanyLicensePrivate.class, matchGroupId, lookUpDevice, lookUpGroup, unwindInfo,
				unwindGroup, countTotal);

		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(CompanyLicensePrivate.class, matchGroupId, lookUpDevice, lookUpGroup, unwindInfo,
					unwindGroup, filtering, countTotal);
		}

		Object count = getCountResult(mongoTemplate.aggregate(aggregation, "license", Object.class));

		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);

		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);

		// IMPORTANT: paginationSkip and paginationLimit must be in correct order, like shown below.
		aggregation = Aggregation.newAggregation(CompanyLicensePrivate.class, matchGroupId, lookUpDevice, lookUpGroup, unwindInfo, unwindGroup,
				paginationSkip, paginationLimit);

		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(CompanyLicensePrivate.class, matchGroupId, lookUpDevice, lookUpGroup, unwindInfo,
					unwindGroup, filtering, paginationSkip, paginationLimit);
		}

		AggregationResults<Device> results = mongoTemplate.aggregate(aggregation, "license", Device.class);

		Map<String, Object> devicesMap = new HashMap<>();
		devicesMap.put("devices", results.getMappedResults());
		devicesMap.put("totalSize", count);

		return devicesMap;
	}

}
