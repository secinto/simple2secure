package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestSequenceRepositoryImpl extends TestSequenceRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testSequence";
		super.className = TestSequence.class;
	}

	@Override
	public List<TestSequence> getByDeviceId(ObjectId deviceId, int page, int size, String filter) {
		
		AggregationOperation matchDeviceId = Aggregation.match(new Criteria("podId").is(deviceId));
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = {"name"};
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(TestSequence.class, matchDeviceId, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(TestSequence.class, matchDeviceId, filtering, countTotal);
		}
		
		//Object count = getCountResult(mongoTemplate.aggregate(aggregation, this.collectionName, Object.class));
		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);
		
		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		aggregation = Aggregation.newAggregation(TestSequence.class, matchDeviceId, paginationSkip, paginationLimit);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(TestSequence.class, matchDeviceId, filtering, paginationSkip, paginationLimit);
		}
		
		AggregationResults<TestSequence> results = mongoTemplate.aggregate(aggregation, this.collectionName, TestSequence.class);
		
		return results.getMappedResults();
	}

	@Override
	public long getCountOfSequencesWithDeviceid(ObjectId deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		long count = mongoTemplate.count(query, TestSequence.class, collectionName);
		return count;
	}

}
