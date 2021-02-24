package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestInputData;
import com.simple2secure.portal.repository.TestInputDataRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestInputDataRepositoryImpl extends TestInputDataRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testInputData"; //$NON-NLS-1$
		super.className = TestInputData.class;
	}

	@Override
	public List<TestInputData> getByTestId(ObjectId testId) {
		Query query = new Query(Criteria.where("testId").is(testId));
		List<TestInputData> testInputData = mongoTemplate.find(query, TestInputData.class);
		return testInputData;
	}
}
