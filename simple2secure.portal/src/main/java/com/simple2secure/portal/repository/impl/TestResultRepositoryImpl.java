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
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.TestRunDTO;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.TestUtils;

@Repository
@Transactional
public class TestResultRepositoryImpl extends TestResultRepository {

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	TestUtils testUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testResult"; //$NON-NLS-1$
		super.className = TestResult.class;
	}

	@Override
	public List<TestResult> getByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<TestResult> testResults = mongoTemplate.find(query, TestResult.class);
		return testResults;
	}

	@Override
	public List<TestResult> getByLicenseId(String licenseId) {
		Query query = new Query(Criteria.where("licenseId").is(licenseId));
		List<TestResult> testResults = mongoTemplate.find(query, TestResult.class);
		return testResults;
	}

	@Override
	public List<TestResult> getByTestId(String testId) {
		Query query = new Query(Criteria.where("testId").is(testId));
		List<TestResult> testResults = mongoTemplate.find(query, TestResult.class);
		return testResults;
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
	public Map<String, Object> getByTestRunIdWithPagination(List<String> testRunIds, int page, int size) {
		Map<String, Object> testResultMap = new HashMap<>();
		if (testRunIds != null && testRunIds.size() > 0) {
			List<TestRunDTO> testRunDto = new ArrayList<>();

			List<Criteria> orExpression = new ArrayList<>();
			Criteria orCriteria = new Criteria();
			Query query = new Query();
			for (String testRunId : testRunIds) {
				Criteria expression = new Criteria();
				expression.and("testRunId").is(testRunId);
				orExpression.add(expression);
			}

			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

			long count = mongoTemplate.count(query, TestResult.class, collectionName);

			int limit = portalUtils.getPaginationLimit(size);
			int skip = portalUtils.getPaginationStart(size, page, limit);

			query.limit(limit);
			query.skip(skip);
			query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

			List<TestResult> testResults = mongoTemplate.find(query, TestResult.class, collectionName);

			testRunDto = testUtils.generateTestRunDTOByTestResults(testResults);

			testResultMap.put("tests", testRunDto);
			testResultMap.put("totalSize", count);
		} else {
			testResultMap.put("tests", new ArrayList<TestRunDTO>());
			testResultMap.put("totalSize", 0);
		}
		return testResultMap;

	}
}
