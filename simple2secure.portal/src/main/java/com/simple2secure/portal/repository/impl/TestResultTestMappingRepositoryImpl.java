package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestResultTestMapping;
import com.simple2secure.portal.repository.TestResultTestMappingRepository;
import com.simple2secure.portal.repository.UserRepository;

@Repository
@Transactional
public class TestResultTestMappingRepositoryImpl extends TestResultTestMappingRepository {

	@Autowired
	UserRepository userRepository;

	@PostConstruct
	public void init() {
		super.collectionName = "testResultTestMapping"; //$NON-NLS-1$
		super.className = TestResultTestMapping.class;
	}

	@Override
	public List<TestResultTestMapping> getByTestId(String testId) {
		Query query = new Query(Criteria.where("testId").is(testId));
		List<TestResultTestMapping> testResultTestMapping = mongoTemplate.find(query, TestResultTestMapping.class);
		return testResultTestMapping;
	}

	@Override
	public List<TestResultTestMapping> getByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		List<TestResultTestMapping> testResultTestMapping = mongoTemplate.find(query, TestResultTestMapping.class);
		return testResultTestMapping;
	}

}
