package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.portal.repository.SequenceRunRepository;

@Repository
@Transactional
public class SequenceRunRepositoryImpl extends SequenceRunRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "sequenceRun"; //$NON-NLS-1$
		super.className = SequenceRun.class;
	}

	@Override
	public List<SequenceRun> getByContextId(String contextId) {
		Query query = new Query(Criteria.where("contextId").is(contextId));
		List<SequenceRun> sequences = mongoTemplate.find(query, SequenceRun.class);
		return sequences;
	}

	@Override
	public List<SequenceRun> getPlannedSequence(String podId) {
		Query query = new Query(Criteria.where("testStatus").is(TestStatus.PLANNED).and("podId").is(podId));
		List<SequenceRun> sequences = mongoTemplate.find(query, SequenceRun.class);
		return sequences;
	}

	@Override
	public List<SequenceRun> getSequenceRunByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		List<SequenceRun> sequences = mongoTemplate.find(query, SequenceRun.class);
		return sequences;
	}

}
