package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Test;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class TestRepositoryImpl extends TestRepository {

	@Autowired
	PortalUtils portalUtils;

	@PostConstruct
	public void init() {
		super.collectionName = "test"; //$NON-NLS-1$
		super.className = Test.class;
	}

	@Override
	public List<Test> getByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getScheduledTest() {
		Query query = new Query(Criteria.where("scheduled").is(true));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public Test getTestByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		Test test = mongoTemplate.findOne(query, Test.class);
		return test;
	}

	@Override
	public Test getTestByNameAndDeviceId(String name, String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("name").is(name));
		Test test = mongoTemplate.findOne(query, Test.class);
		return test;
	}

	@Override
	public List<Test> getByDeviceIdWithPagination(String deviceId, int page, int size, boolean usePagination) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("deleted").is(false));
		int limit = portalUtils.getPaginationLimit(size);
		int skip = portalUtils.getPaginationStart(size, page, limit);
		if (usePagination) {
			query.limit(limit);
			query.skip(skip);
		}
		query.with(Sort.by(Sort.Direction.DESC, "lastChangedTimestamp"));
		List<Test> tests = mongoTemplate.find(query, Test.class, collectionName);
		return tests;
	}

	@Override
	public long getCountOfTestsWithDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("deleted").is(false));
		long count = mongoTemplate.count(query, Test.class, collectionName);
		return count;
	}

	@Override
	public List<Test> getNewPortalTestsByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("newTest").is(true));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getDeletedTestsByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("deleted").is(true));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

	@Override
	public List<Test> getUnsyncedTestsByDeviceId(String deviceId) {
		Query query = new Query(Criteria.where("podId").is(deviceId).and("synced").is(false));
		List<Test> tests = mongoTemplate.find(query, Test.class);
		return tests;
	}

}
