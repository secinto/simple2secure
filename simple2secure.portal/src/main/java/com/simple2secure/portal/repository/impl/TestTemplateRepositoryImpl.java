package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestCaseTemplate;
import com.simple2secure.portal.repository.TestTemplateRepository;

@Repository
@Transactional
public class TestTemplateRepositoryImpl extends TestTemplateRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testCaseTemplate"; //$NON-NLS-1$
		super.className = TestCaseTemplate.class;
	}

	@Override
	public List<TestCaseTemplate> getByToolId(String toolId) {
		Query query = new Query(Criteria.where("toolId").is(toolId));
		List<TestCaseTemplate> testTemplateList = mongoTemplate.find(query, TestCaseTemplate.class);
		return testTemplateList;
	}
}
