package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestCaseSequence;
import com.simple2secure.portal.repository.TestSequenceRepository;

@Repository
@Transactional
public class TestSequenceRepositoryImpl extends TestSequenceRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testCaseSequence"; //$NON-NLS-1$
		super.className = TestCaseSequence.class;
	}

	@Override
	public List<TestCaseSequence> getAllIsFinishedAndScheduled(boolean isFinished, boolean isScheduled) {
		Query query = new Query(Criteria.where("finished").is(isFinished).and("scheduled").is(isScheduled));
		List<TestCaseSequence> testSequence = mongoTemplate.find(query, TestCaseSequence.class);
		return testSequence;
	}
}
