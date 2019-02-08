package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestCase;
import com.simple2secure.portal.repository.TestRepository;

@Repository
@Transactional
public class TestRepositoryImpl extends TestRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testCase"; //$NON-NLS-1$
		super.className = TestCase.class;
	}

	@Override
	public List<TestCase> getByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		List<TestCase> tests = mongoTemplate.find(query, TestCase.class);
		return tests;
	}
}
