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

import com.simple2secure.api.dto.TestRunDTO;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestStatus;
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
	public Map<String, Object> getByContextIdForPagination(String contextId, int page, int size) {

		List<TestRunDTO> testRunDto = new ArrayList<>();

		Query query = new Query(Criteria.where("contextId").is(contextId));
		long count = mongoTemplate.count(query, TestResult.class, collectionName);

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
		List<TestRun> tests = mongoTemplate.find(query, TestRun.class, collectionName);

		for (TestRun testRun : tests) {
			TestResult testResult = testResultRepository.getByTestRunId(testRun.getId());
			testRunDto.add(new TestRunDTO(testRun, testResult));
		}

		Map<String, Object> testRunObject = new HashMap<>();
		testRunObject.put("tests", testRunDto);
		testRunObject.put("totalSize", count);

		return testRunObject;
	}

	@Override
	public List<TestRun> getPlannedTests(String deviceId) {
		Query query = new Query(Criteria.where("testStatus").is(TestStatus.PLANNED).and("podId").is(deviceId));
		List<TestRun> tests = mongoTemplate.find(query, TestRun.class);
		return tests;
	}

	@Override
	public List<TestRun> getByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<TestRun> tests = mongoTemplate.find(query, TestRun.class);
		return tests;
	}

}
