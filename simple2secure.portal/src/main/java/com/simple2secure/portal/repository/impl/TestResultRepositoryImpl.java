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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestResult;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestResultRepositoryImpl extends TestResultRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testResult"; //$NON-NLS-1$
		super.className = TestResult.class;
	}

	@Override
	public TestResult getByTestRunId(ObjectId testRunId) {
		Query query = new Query(Criteria.where("testRunId").is(testRunId));
		TestResult testResult = mongoTemplate.findOne(query, TestResult.class);
		return testResult;
	}

	@Override
	public List<TestResult> getSearchQueryByTestRunId(String searchQuery, ObjectId testRunId) {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		Query query = TextQuery.queryText(criteria).sortByScore();
		query.addCriteria(Criteria.where("testRunId").is(testRunId));
		List<TestResult> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

	@Override
	public List<TestResult> getByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		List<TestResult> testResult = mongoTemplate.find(query, TestResult.class);
		return testResult;
	}

	@Override
	public Map<String, Object> getTestResultsByDeviceIdWithPagination(List<ObjectId> deviceIds, int page, int size, String filter) {
		long count = 0;
		Map<String, Object> testResultMap = new HashMap<>();
		if (!deviceIds.isEmpty()) {

			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();

			for (ObjectId deviceId : deviceIds) {
				Criteria expression = new Criteria();
				expression.and("deviceId").is(deviceId);
				orExpression.add(expression);
			}

			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

			if (!Strings.isBlank(filter)) {
				query.addCriteria(Criteria.where("name").regex(filter, "i"));
			}

			count = mongoTemplate.count(query, TestResult.class, collectionName);
			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
			query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

			List<TestResult> reports = mongoTemplate.find(query, TestResult.class, collectionName);

			testResultMap.put("report", reports);
			testResultMap.put("totalSize", count);
		}
		return testResultMap;
	}
}
