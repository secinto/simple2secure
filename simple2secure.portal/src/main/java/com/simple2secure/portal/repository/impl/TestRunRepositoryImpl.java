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


import com.simple2secure.api.dto.TestRunDTO;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.Context;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestRunRepositoryImpl extends TestRunRepository {

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	TestResultRepository testResultRepository;

	@PostConstruct
	public void init() {
		super.collectionName = "testRun"; //$NON-NLS-1$
		super.className = TestRun.class; 
	}

	@Override
	public Map<String, Object> getByContextIdForPagination(ObjectId contextId, int page, int size, String filter) {		
		AggregationOperation matchContextId = Aggregation.match(new Criteria("_id").is(contextId));
		AggregationOperation lookUpRuns = Aggregation.lookup("testRun", "_id", "contextId", "testRun");
		AggregationOperation unwindRuns = Aggregation.unwind("testRun", true);
		AggregationOperation lookUpResult = Aggregation.lookup("testResult", "testRun._id", "testRunId", "testResult");
		AggregationOperation unwindResults = Aggregation.unwind("testResult", true);
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = { "testRun.testName", "testRun.testRunType", "testRun.testStatus", "testResult.hostname"};
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResult, unwindResults, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResult, unwindResults, filtering, countTotal);
		}
		
		Object count = getCountResult(mongoTemplate.aggregate(aggregation, "context", Object.class));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);
		
		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "testRun.timestamp");
		
		aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResult, unwindResults, sort, paginationSkip, paginationLimit);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContextId, lookUpRuns, unwindRuns, lookUpResult, unwindResults, filtering, sort, paginationSkip, paginationLimit);
		}
		
		AggregationResults<TestRunDTO> results = mongoTemplate.aggregate(aggregation, "context", TestRunDTO.class);
		List<TestRunDTO> tests = results.getMappedResults();


		Map<String, Object> testRunObject = new HashMap<>();
		testRunObject.put("tests", tests);
		testRunObject.put("totalSize", count);

		return testRunObject;
	}
	
    @Override
    public List<TestRun> getPlannedTests(ObjectId deviceId) {
        Query query = new Query(Criteria.where("testStatus").is(TestStatus.PLANNED).and("podId").is(deviceId));
        // Query query = new Query(Criteria.where("podId").is(deviceId));
        List<TestRun> tests = mongoTemplate.find(query, TestRun.class);
        return tests;
    }

	@Override
	public List<TestRun> getByContextId(ObjectId contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<TestRun> tests = mongoTemplate.find(query, TestRun.class);
		return tests;
	}

}
