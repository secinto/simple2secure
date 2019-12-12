package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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
	public TestResult getByTestRunId(String testRunId) {
		Query query = new Query(Criteria.where("testRunId").is(testRunId));
		TestResult testResult = mongoTemplate.findOne(query, TestResult.class);
		return testResult;
	}

	@Override
	public List<TestResult> getSearchQueryByTestRunId(String searchQuery, String testRunId) {
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchQuery);
		Query query = TextQuery.queryText(criteria).sortByScore();
		query.addCriteria(Criteria.where("testRunId").is(testRunId));
		List<TestResult> result = mongoTemplate.find(query, className, collectionName);
		return result;
	}

	@Override
	public long getTotalAmountOfTestResults(List<String> testRunIds) {
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String testRunId : testRunIds) {
			Criteria expression = new Criteria();
			expression.and("testRunId").is(testRunId);
			orExpression.add(expression);
		}

		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		return mongoTemplate.count(query, TestResult.class, collectionName);
	}

	@Override
	public List<TestResult> getByTestRunIdWithPagination(List<String> testRunIds, int page, int size) {

		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String testRunId : testRunIds) {
			Criteria expression = new Criteria();
			expression.and("testRunId").is(testRunId);
			orExpression.add(expression);
		}

		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

		return mongoTemplate.find(query, TestResult.class, collectionName);

	}
}
