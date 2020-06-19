package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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
	public SystemUnderTest getByDeviceId(String endDeviceId) {
		Query query = new Query(Criteria.where("deviceId").is(endDeviceId));
		SystemUnderTest sut = mongoTemplate.findOne(query, SystemUnderTest.class);
		return sut;
	}

	@Override
	public long getTotalAmountOfSystemUnderTest(String contextId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("contextId").is(contextId));
		return mongoTemplate.count(query, SystemUnderTest.class, collectionName);
	}

	@Override
	public long getTotalAmountOfSystemUnderTestWithType(String contextId, String deviceType) {
		Query query = new Query();
		query.addCriteria(Criteria.where("contextId").is(contextId).and("systemType").is(deviceType));
		return mongoTemplate.count(query, SystemUnderTest.class, collectionName);
	}

	@Override
	public List<SystemUnderTest> getAllByContextIdPaged(String contextId, int page, int size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("contextId").is(contextId));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "lastOnlineTimestamp"));
		return mongoTemplate.find(query, SystemUnderTest.class, collectionName);
	}

	@Override
	public List<SystemUnderTest> getAllByContextIdAndSystemTypePaged(String contextId, int page, int size, String type) {
		Query query = new Query();
		query.addCriteria(Criteria.where("contextId").is(contextId).and("systemType").is(type));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "lastOnlineTimestamp"));
		return mongoTemplate.find(query, SystemUnderTest.class, collectionName);
	}

	@Override
	public List<SystemUnderTest> getApplicableSystemUnderTests(List<String> sutMetadataKeyList) {
		List<Criteria> criterias = new ArrayList<>();
		Criteria[] criteriaArr = new Criteria[criterias.size()];
		Criteria criteria = new Criteria();
		for (String key : sutMetadataKeyList) {
			criterias.add(new Criteria("metadata." + key).exists(true));
		}
		criteria = criteria.andOperator(criterias.toArray(criteriaArr));

		MatchOperation matchStage = Aggregation.match(criteria);
		Aggregation aggregation = Aggregation.newAggregation(matchStage);
		AggregationResults<SystemUnderTest> result = mongoTemplate.aggregate(aggregation, "systemUnderTest", SystemUnderTest.class);
		return result.getMappedResults();
	}

}