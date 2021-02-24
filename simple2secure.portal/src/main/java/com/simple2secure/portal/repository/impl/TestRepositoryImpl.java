package com.simple2secure.portal.repository.impl;

import java.util.List;

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

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.Test;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestRepositoryImpl extends TestRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "test"; //$NON-NLS-1$
		super.className = Test.class;
	}

	@Override
	public List<Test> getByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getScheduledTest() {
		Query query = new Query(Criteria.where("scheduled").is(true));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public Test getTestByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		Test test = mongoTemplate.findOne(query, Test.class);
		return test;
	}

	@Override
	public Test getTestByNameAndDeviceId(String name, ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("name").is(name));
		Test test = mongoTemplate.findOne(query, Test.class);
		return test;
	}

	@Override
	public List<Test> getByDeviceIdWithPagination(ObjectId deviceId, int page, int size, boolean usePagination, String filter) {
		
		AggregationOperation matchDeviceId = Aggregation.match(new Criteria("podId").is(deviceId));
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = {"name"};
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(Test.class, matchDeviceId, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Test.class, matchDeviceId, filtering, countTotal);
		}
		
		//Object count = getCountResult(mongoTemplate.aggregate(aggregation, this.collectionName, Object.class));
		
		if (usePagination) {
			int limit = portalUtils.getPaginationLimit(size);
			long skip = portalUtils.getPaginationStart(size, page, limit);
			
			AggregationOperation paginationLimit = Aggregation.limit(limit);
			AggregationOperation paginationSkip = Aggregation.skip(skip);
			
			aggregation = Aggregation.newAggregation(Test.class, matchDeviceId, paginationSkip, paginationLimit);
			
			if (!Strings.isBlank(filter)) {
				aggregation = Aggregation.newAggregation(Test.class, matchDeviceId, filtering, paginationSkip, paginationLimit);
			}
		}else {
			aggregation = Aggregation.newAggregation(Test.class, matchDeviceId);
		}
		
		AggregationResults<Test> results = mongoTemplate.aggregate(aggregation, this.collectionName, Test.class);
		
		return results.getMappedResults();
	}

	@Override
	public long getCountOfTestsWithDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		long count = mongoTemplate.count(query, Test.class, collectionName);
		return count;
	}

	@Override
	public List<Test> getNewPortalTestsByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getDeletedTestsByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getUnsyncedTestsByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

}
