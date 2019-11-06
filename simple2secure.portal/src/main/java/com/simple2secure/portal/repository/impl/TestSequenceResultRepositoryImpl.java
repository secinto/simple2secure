package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestResult;

//import javax.annotation.PostConstruct;

import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.portal.repository.TestSequenceResultRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestSequenceResultRepositoryImpl extends TestSequenceResultRepository {
	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testSequenceResult"; //$NON-NLS-1$
		super.className = TestSequenceResult.class;
	}

	@Override
	public List<TestSequenceResult> getBySequenceId(String sequenceId) {
		Query query = new Query(Criteria.where("sequence_id").is(sequenceId));
		List<TestSequenceResult> testSequenceResults = mongoTemplate.find(query, TestSequenceResult.class);
		return testSequenceResults;
	}

	@Override
	public TestSequenceResult getBySequenceRunId(String sequenceRunId) {
		Query query = new Query(Criteria.where("sequence_run_id").is(sequenceRunId));
		TestSequenceResult testSequenceResults = mongoTemplate.findOne(query, TestSequenceResult.class);
		return testSequenceResults;
	}

	@Override
	public List<TestSequenceResult> getByPodId(String podId) {
		Query query = new Query(Criteria.where("pod_id").is(podId));
		List<TestSequenceResult> result = mongoTemplate.find(query, TestSequenceResult.class);
		return result;
	}

	@Override
	public List<TestSequenceResult> getBySequenceRunIds(List<String> sequenceRunIds, int page, int size) {

		List<TestSequenceResult> results = new ArrayList<>();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String sequenceRunId : sequenceRunIds) {
			Criteria expression = new Criteria();
			expression.and("sequence_run_id").is(sequenceRunId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);

		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "time_stamp"));

		results = mongoTemplate.find(query, TestSequenceResult.class, collectionName);

		return results;

	}

	@Override
	public long getCountOfSequencesWithSequenceRunIds(List<String> sequenceRunIds) {
		List<Criteria> orExpression = new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Query query = new Query();
		for (String sequenceRunId : sequenceRunIds) {
			Criteria expression = new Criteria();
			expression.and("sequence_run_id").is(sequenceRunId);
			orExpression.add(expression);
		}
		query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));

		long count = mongoTemplate.count(query, TestResult.class, collectionName);

		return count;
	}

}
