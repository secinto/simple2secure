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
	public Map<String, Object> getByGroupIdsAndType(List<String> groupIds, int page, int size, String deviceType) {
		List<SystemUnderTest> suts = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Map<String, Object> sutMap = new HashMap<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String groupId : groupIds) {
			Criteria expression = new Criteria();
			expression.and("groupId").is(groupId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		query.addCriteria(Criteria.where("endDeviceType").is(deviceType));

		long count = mongoTemplate.count(query, SystemUnderTest.class, collectionName);

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "startTime"));
		suts = mongoTemplate.find(query, SystemUnderTest.class, collectionName);

		sutMap.put("sutList", suts);
		sutMap.put("totalSize", count);

		return sutMap;
	}
}