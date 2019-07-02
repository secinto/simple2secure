package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestRun;
import com.simple2secure.portal.repository.TestRunRepository;

@Repository
@Transactional
public class TestRunRepositoryImpl extends TestRunRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testRun"; //$NON-NLS-1$
		super.className = TestRun.class;
	}

	@Override
	public List<TestRun> getTestNotExecutedByPodId(String podId) {
		Query query = new Query(Criteria.where("executed").is(false).and("podId").is(podId));
		List<TestRun> tests = mongoTemplate.find(query, TestRun.class);
		return tests;
	}

}
