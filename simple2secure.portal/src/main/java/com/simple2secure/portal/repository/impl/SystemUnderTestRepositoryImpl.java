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

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.repository.SystemUnderTestRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class SystemUnderTestRepositoryImpl extends SystemUnderTestRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "systemUnderTest";
		super.className = SystemUnderTest.class;
	}

	@Override
	public SystemUnderTest getByEndDeviceId(String endDeviceId) {
		Query query = new Query(Criteria.where("endDeviceId").is(endDeviceId));
		SystemUnderTest sut = mongoTemplate.findOne(query, SystemUnderTest.class);
		return sut;
	}

	@Override
	public long getTotalAmountOfSystemUnderTest(String contextId, String deviceType) {
		Query query = new Query();
		Criteria expression = new Criteria();
		expression.and("contextId").is(contextId);
		query.addCriteria(Criteria.where("endDeviceType").is(deviceType));
		query.addCriteria(expression);

		return mongoTemplate.count(query, SystemUnderTest.class, collectionName);
	}

	@Override
	public List<SystemUnderTest> getByContextIdAndType(String contextId, int page, int size, String deviceType) {
		Query query = new Query();
		Criteria expression = new Criteria();
		expression.and("contextId").is(contextId);
		query.addCriteria(Criteria.where("endDeviceType").is(deviceType));
		query.addCriteria(expression);

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "startTime"));
		return mongoTemplate.find(query, SystemUnderTest.class, collectionName);
	}
}