package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

//import javax.annotation.PostConstruct;

import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.portal.repository.TestSequenceResultRepository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TestSequenceResultRepositoryImpl extends TestSequenceResultRepository {
    @PostConstruct
    public void init() {
        super.collectionName = "testSequenceResult"; //$NON-NLS-1$
        super.className = TestSequenceResult.class;
    }

    @Override
    public List<TestSequenceResult> getBySequenceId(String sequenceId) {
        Query query = new Query(Criteria.where("sequenceId").is(sequenceId));
		List<TestSequenceResult> testSequenceResults = mongoTemplate.find(query, TestSequenceResult.class);
		return testSequenceResults;
    }

    @Override
    public TestSequenceResult getBySequenceRunId(String sequenceRunId) {
        Query query = new Query(Criteria.where("sequenceRunId").is(sequenceRunId));
		TestSequenceResult testSequenceResults = mongoTemplate.findOne(query, TestSequenceResult.class);
		return testSequenceResults;
    }

}
