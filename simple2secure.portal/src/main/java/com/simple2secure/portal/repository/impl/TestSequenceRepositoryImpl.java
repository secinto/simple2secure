package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.repository.TestSequenceRepository;

@Repository
@Transactional
public class TestSequenceRepositoryImpl extends TestSequenceRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "testSequence";
		super.className = TestSequence.class;
	}

	@Override
	public List<TestSequence> getByPodId(String podId) {
		Query query = new Query(Criteria.where("podId").is(podId));
		List<TestSequence> testSequences = mongoTemplate.find(query, TestSequence.class);
		return testSequences;
	}

	@Override
	public TestSequence getSequenceByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		TestSequence sequence = mongoTemplate.findOne(query, TestSequence.class);
		return sequence;
	}

	@Override
	public TestSequence getSequenceByNameAndPodId(String name, String podId) {
		Query query = new Query(Criteria.where("podId").is(podId).and("name").is(name));
		TestSequence sequence = mongoTemplate.findOne(query, TestSequence.class);
		return sequence;
	}

}
