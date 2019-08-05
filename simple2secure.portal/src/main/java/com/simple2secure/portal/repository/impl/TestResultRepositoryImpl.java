package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestResult;
import com.simple2secure.portal.repository.TestResultRepository;

@Repository
@Transactional
public class TestResultRepositoryImpl extends TestResultRepository {

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
	public List<TestResult> getByTestRunId(String testRunId) {
		Query query = new Query(Criteria.where("testRunId").is(testRunId));
		List<TestResult> testResults = mongoTemplate.find(query, TestResult.class);
		return testResults;
	}
}
