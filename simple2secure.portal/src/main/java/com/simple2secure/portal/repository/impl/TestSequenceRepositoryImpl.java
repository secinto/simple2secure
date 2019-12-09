package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestSequenceRepositoryImpl extends TestSequenceRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "testSequence";
		super.className = TestSequence.class;
	}

	@Override
	public List<TestSequence> getByDeviceId(String deviceId, int page, int size) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);
		query.limit(limit);
		query.skip(skip);
		query.with(Sort.by(Sort.Direction.DESC, "lastChangedTimeStamp"));
		List<TestSequence> testSequences = mongoTemplate.find(query, TestSequence.class, collectionName);
		return testSequences;
	}

	@Override
	public long getCountOfSequencesWithDeviceid(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		long count = mongoTemplate.count(query, TestSequence.class, collectionName);
		return count;
	}

}
