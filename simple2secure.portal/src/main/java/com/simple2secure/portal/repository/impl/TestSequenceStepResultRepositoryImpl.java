package com.simple2secure.portal.repository.impl;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//import javax.annotation.PostConstruct;

import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestSequenceStepResult;
import com.simple2secure.portal.repository.TestSequenceStepResultRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestSequenceStepResultRepositoryImpl extends TestSequenceStepResultRepository {
	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testSequenceStepResult"; //$NON-NLS-1$
		super.className = TestSequenceStepResult.class;
	}

	@Override
	public TestSequenceResult getBySequenceRunId(ObjectId sequenceRunId) {
		Query query = new Query(Criteria.where("sequenceRunId").is(sequenceRunId));
		TestSequenceResult testSequenceResults = mongoTemplate.findOne(query, TestSequenceResult.class);
		return testSequenceResults;
	}
}
