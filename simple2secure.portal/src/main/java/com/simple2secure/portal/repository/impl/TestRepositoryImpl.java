package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Test;
import com.simple2secure.portal.repository.TestRepository;

@Repository
@Transactional
public class TestRepositoryImpl extends TestRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "test"; //$NON-NLS-1$
		super.className = Test.class;
	}

	@Override
	public List<Test> getByPodId(String podId) {
		Query query = new Query(Criteria.where("podId").is(podId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}
}
