package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestCaseResult;
import com.simple2secure.portal.repository.TestResultRepository;

@Repository
@Transactional
public class TestResultRepositoryImpl extends TestResultRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testCaseResult"; //$NON-NLS-1$
		super.className = TestCaseResult.class;
	}

	@Override
	public List<TestCaseResult> findByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		List<TestCaseResult> testResultList = mongoTemplate.find(query, TestCaseResult.class);
		return testResultList;
	}

}
